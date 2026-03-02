import api from "../api";
import { Activity, DashboardMetricsResponse, DashboardStats, VehicleStats } from "../types";

const safeNumber = (value: unknown, fallback = 0) => (typeof value === "number" && !Number.isNaN(value) ? value : fallback);

export const fetchDashboardStats = async (): Promise<DashboardStats> => {
  const [vehicleStats, metrics] = await Promise.allSettled([
    api.get<VehicleStats>("/api/vehicles/stats"),
    api.get<DashboardMetricsResponse>("/api/dashboard/metrics"),
  ]);

  if (vehicleStats.status !== "fulfilled") {
    throw vehicleStats.reason;
  }

  const base = vehicleStats.value;
  const metricsData = metrics.status === "fulfilled" ? metrics.value : {};
  const utilizationRate =
    base.totalVehicles > 0 ? Math.round((safeNumber(base.rentedVehicles) / safeNumber(base.totalVehicles, 1)) * 100) : 0;

  return {
    ...base,
    todayInspections: safeNumber(metricsData.todayInspections),
    pendingApprovals: safeNumber(metricsData.pendingApprovals),
    activeDefects: safeNumber(metricsData.activeDefects),
    overdueInspections: safeNumber(metricsData.overdueInspections),
    inspectionsTrend: safeNumber(metricsData.inspectionsTrend),
    defectsTrend: safeNumber(metricsData.defectsTrend),
    utilizationRate,
  };
};

export const fetchDashboardActivities = async (): Promise<Activity[]> => {
  return api.get<Activity[]>("/api/activities");
};
