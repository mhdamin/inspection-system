import React, { useMemo, useState } from "react";
import { useQuery } from "@tanstack/react-query";
import {
  AlertCircle,
  AlertTriangle,
  Car,
  CheckCircle,
  ClipboardList,
  Clock,
  Key,
  TrendingDown,
  TrendingUp,
  Wrench,
} from "lucide-react";
import PageHeader from "./ui/PageHeader";
import { ErrorState, LoadingState, EmptyState } from "./ui/StatePanel";
import { Activity, Alert, DashboardStats } from "../types";
import { fetchDashboardActivities, fetchDashboardStats } from "../services/dashboardService";
import { auth } from "../config";

const Dashboard: React.FC = () => {
  const username = auth.getUsername() || "User";
  const isManager = auth.hasRole("ROLE_MANAGER");
  const isAdmin = auth.hasRole("ROLE_ADMIN") || auth.hasRole("ROLE_SUPERADMIN");
  const [activityFilter, setActivityFilter] = useState("all");

  const statsQuery = useQuery({
    queryKey: ["dashboard", "stats"],
    queryFn: fetchDashboardStats,
  });

  const activityQuery = useQuery({
    queryKey: ["dashboard", "activities"],
    queryFn: fetchDashboardActivities,
  });

  const stats = statsQuery.data;
  const activities = activityQuery.data || [];

  const alerts = useMemo(() => generateAlerts(stats, isManager), [stats, isManager]);
  const filteredActivities = useMemo(
    () =>
      activityFilter === "all"
        ? activities
        : activities.filter((item) => item.changeType.toLowerCase() === activityFilter.toLowerCase()),
    [activities, activityFilter],
  );

  if (statsQuery.isLoading || activityQuery.isLoading) {
    return <LoadingState message="Loading dashboard..." />;
  }

  if (statsQuery.isError) {
    return <ErrorState message="Unable to load dashboard metrics." onRetry={() => statsQuery.refetch()} />;
  }

  if (!stats) {
    return <EmptyState title="No dashboard data yet" description="Dashboard metrics will appear once operations start." />;
  }

  return (
    <div className="space-y-6">
      <PageHeader
        title={`Welcome back, ${username}`}
        description="Real-time fleet and inspection operations snapshot."
      />

      <div className="grid grid-cols-1 gap-4 md:grid-cols-2 xl:grid-cols-4">
        <StatCard title="Total Fleet" value={stats.totalVehicles} icon={<Car size={18} />} color="blue" />
        <StatCard title="Available" value={stats.availableVehicles} icon={<CheckCircle size={18} />} color="green" />
        <StatCard
          title="Rented Out"
          value={stats.rentedVehicles}
          icon={<Key size={18} />}
          color="amber"
          trend={stats.utilizationRate > 70 ? "up" : "down"}
          trendValue={stats.utilizationRate}
        />
        <StatCard title="Maintenance" value={stats.maintenanceVehicles} icon={<Wrench size={18} />} color="red" />
      </div>

      <div className="grid grid-cols-1 gap-4 md:grid-cols-2 xl:grid-cols-4">
        <StatCard
          title="Inspections Today"
          value={stats.todayInspections}
          icon={<ClipboardList size={18} />}
          color="purple"
          trend={stats.inspectionsTrend > 0 ? "up" : "down"}
          trendValue={stats.inspectionsTrend}
        />
        {(isAdmin || isManager) && (
          <StatCard title="Pending Approvals" value={stats.pendingApprovals} icon={<Clock size={18} />} color="orange" />
        )}
        <StatCard
          title="Active Defects"
          value={stats.activeDefects}
          icon={<AlertTriangle size={18} />}
          color="rose"
          trend={stats.defectsTrend > 0 ? "up" : "down"}
          trendValue={Math.abs(stats.defectsTrend)}
        />
        <StatCard
          title="Overdue"
          value={stats.overdueInspections}
          icon={<AlertCircle size={18} />}
          color={stats.overdueInspections > 0 ? "red" : "green"}
        />
      </div>

      {alerts.length > 0 && (
        <section className="rounded-panel border border-slate-200 bg-white shadow-panel">
          <div className="border-b border-slate-100 px-5 py-3">
            <h3 className="text-sm font-semibold text-slate-800">Operational Alerts</h3>
          </div>
          <div className="divide-y divide-slate-100">
            {alerts.map((alert) => (
              <AlertRow key={alert.id} alert={alert} />
            ))}
          </div>
        </section>
      )}

      <section className="rounded-panel border border-slate-200 bg-white shadow-panel">
        <div className="flex items-center justify-between border-b border-slate-100 px-5 py-3">
          <h3 className="text-sm font-semibold text-slate-800">Recent Activities</h3>
          <select
            aria-label="Filter activities"
            value={activityFilter}
            onChange={(event) => setActivityFilter(event.target.value)}
            className="rounded-lg border border-slate-200 px-3 py-1.5 text-sm text-slate-700"
          >
            <option value="all">All</option>
            <option value="temporary">Rentals</option>
            <option value="return">Returns</option>
            <option value="permanent">Permanent</option>
          </select>
        </div>
        <div className="max-h-96 space-y-3 overflow-auto px-5 py-4">
          {activityQuery.isError ? (
            <ErrorState message="Unable to load activity feed." onRetry={() => activityQuery.refetch()} />
          ) : filteredActivities.length === 0 ? (
            <EmptyState title="No activity available" />
          ) : (
            filteredActivities.slice(0, 10).map((activity) => <ActivityRow key={activity.id} activity={activity} />)
          )}
        </div>
      </section>
    </div>
  );
};

const generateAlerts = (stats: DashboardStats | undefined, isManager: boolean): Alert[] => {
  if (!stats) return [];
  const result: Alert[] = [];

  if (stats.overdueInspections > 0) {
    result.push({
      id: "overdue",
      type: "critical",
      title: "Overdue inspections",
      message: `${stats.overdueInspections} vehicle inspections require immediate action.`,
      timestamp: new Date().toISOString(),
    });
  }

  if (stats.maintenanceVehicles > 5) {
    result.push({
      id: "maintenance",
      type: "warning",
      title: "High maintenance load",
      message: `${stats.maintenanceVehicles} vehicles are currently in maintenance.`,
      timestamp: new Date().toISOString(),
    });
  }

  if (stats.pendingApprovals > 0 && isManager) {
    result.push({
      id: "pending",
      type: "info",
      title: "Pending approvals",
      message: `${stats.pendingApprovals} inspections are waiting for manager approval.`,
      timestamp: new Date().toISOString(),
    });
  }

  return result;
};

const AlertRow: React.FC<{ alert: Alert }> = ({ alert }) => {
  const colorByType = {
    critical: "border-red-500 bg-red-50 text-red-700",
    warning: "border-amber-500 bg-amber-50 text-amber-700",
    info: "border-blue-500 bg-blue-50 text-blue-700",
  } as const;

  return (
    <div className={`border-l-4 px-5 py-4 ${colorByType[alert.type]}`}>
      <p className="text-sm font-semibold">{alert.title}</p>
      <p className="mt-1 text-sm">{alert.message}</p>
    </div>
  );
};

const ActivityRow: React.FC<{ activity: Activity }> = ({ activity }) => {
  const time = new Date(activity.timestamp).toLocaleString();
  return (
    <div className="rounded-lg border border-slate-200 bg-slate-50 px-4 py-3">
      <p className="text-sm font-medium text-slate-800">{activity.changeType}</p>
      <p className="text-sm text-slate-600">
        {activity.oldVehiclePlate || "N/A"} to {activity.newVehiclePlate || "N/A"}
      </p>
      <p className="text-xs text-slate-500">
        {activity.staffName} at {time}
      </p>
    </div>
  );
};

const StatCard: React.FC<{
  title: string;
  value: number;
  icon: React.ReactNode;
  color: "blue" | "green" | "amber" | "red" | "purple" | "orange" | "rose";
  trend?: "up" | "down";
  trendValue?: number;
}> = ({ title, value, icon, color, trend, trendValue }) => {
  const colorClasses = {
    blue: "bg-blue-50 text-blue-700",
    green: "bg-green-50 text-green-700",
    amber: "bg-amber-50 text-amber-700",
    red: "bg-red-50 text-red-700",
    purple: "bg-purple-50 text-purple-700",
    orange: "bg-orange-50 text-orange-700",
    rose: "bg-rose-50 text-rose-700",
  } as const;

  return (
    <article className="rounded-panel border border-slate-200 bg-white p-5 shadow-panel">
      <div className="mb-3 flex items-center justify-between">
        <p className="text-sm font-medium text-slate-500">{title}</p>
        <div className={`rounded-lg p-2 ${colorClasses[color]}`}>{icon}</div>
      </div>
      <div className="flex items-end gap-2">
        <p className="text-2xl font-bold text-slate-900">{value}</p>
        {trend && trendValue !== undefined && (
          <span className={`inline-flex items-center text-xs font-semibold ${trend === "up" ? "text-green-600" : "text-red-600"}`}>
            {trend === "up" ? <TrendingUp size={12} /> : <TrendingDown size={12} />}
            <span className="ml-1">{trendValue}%</span>
          </span>
        )}
      </div>
    </article>
  );
};

export default Dashboard;
