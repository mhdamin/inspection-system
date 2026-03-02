import React, { useState, useEffect } from 'react';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, LineChart, Line, PieChart, Pie, Cell } from 'recharts';
import { Calendar, RefreshCw, AlertCircle, TrendingUp, FileText, AlertTriangle } from 'lucide-react';
import api from '../api';

interface InspectionSummary {
  totalInspections: number;
  preRentalInspections: number;
  postRentalInspections: number;
  periodicInspections: number;
  totalDefects: number;
  unresolvedDefects: number;
  averageDefectsPerInspection: number;
}

interface InspectionTrend {
  date: string;
  inspectionCount: number;
  defectCount: number;
}

interface DefectBreakdown {
  defectType: string;
  count: number;
  percentage: number;
}

interface VehicleStatusTrend {
  date: string;
  availableCount: number;
  rentedCount: number;
  maintenanceCount: number;
}

const DEFECT_COLORS = {
  SCRATCH: '#fbbf24', // Yellow
  DENT: '#f97316',    // Orange
  CRACK: '#ef4444',   // Red
};

const Reports: React.FC = () => {
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [refreshing, setRefreshing] = useState(false);

  // Date range (default: last 30 days)
  const [endDate, setEndDate] = useState(new Date().toISOString().split('T')[0]);
  const [startDate, setStartDate] = useState(() => {
    const date = new Date();
    date.setDate(date.getDate() - 30);
    return date.toISOString().split('T')[0];
  });

  // Report data
  const [summary, setSummary] = useState<InspectionSummary | null>(null);
  const [inspectionTrends, setInspectionTrends] = useState<InspectionTrend[]>([]);
  const [defectBreakdown, setDefectBreakdown] = useState<DefectBreakdown[]>([]);
  const [vehicleStatusTrends, setVehicleStatusTrends] = useState<VehicleStatusTrend[]>([]);

  useEffect(() => {
    fetchReports();
  }, [startDate, endDate]);

  const fetchReports = async () => {
    setIsLoading(true);
    setError(null);

    try {
      const params = `?startDate=${startDate}&endDate=${endDate}`;

      const [summaryData, trendsData, breakdownData, statusData] = await Promise.all([
        api.get<InspectionSummary>(`/api/reports/inspection-summary${params}`),
        api.get<InspectionTrend[]>(`/api/reports/inspection-trends${params}`),
        api.get<DefectBreakdown[]>(`/api/reports/defect-breakdown${params}`),
        api.get<VehicleStatusTrend[]>(`/api/reports/vehicle-status-trends${params}`),
      ]);

      setSummary(summaryData);
      setInspectionTrends(trendsData);
      setDefectBreakdown(breakdownData);
      setVehicleStatusTrends(statusData);
    } catch (err) {
      console.error('Error fetching reports:', err);
      setError('Failed to load reports. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  const refreshReports = async () => {
    setRefreshing(true);
    await fetchReports();
    setRefreshing(false);
  };

  const handleQuickFilter = (days: number) => {
    const end = new Date();
    const start = new Date();
    start.setDate(start.getDate() - days);

    setEndDate(end.toISOString().split('T')[0]);
    setStartDate(start.toISOString().split('T')[0]);
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center py-20">
        <div className="text-center">
          <RefreshCw className="animate-spin mx-auto mb-4 text-gray-400" size={40} />
          <div className="text-gray-500">Loading reports...</div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-red-50 border border-red-200 rounded-xl p-6 text-center">
        <AlertCircle className="mx-auto mb-4 text-red-500" size={48} />
        <h3 className="text-lg font-semibold text-red-700 mb-2">Error Loading Reports</h3>
        <p className="text-red-600 mb-4">{error}</p>
        <button
          onClick={fetchReports}
          className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors"
        >
          Try Again
        </button>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex justify-between items-start">
        <div>
          <h2 className="text-2xl font-bold text-gray-800">Reports & Analytics</h2>
          <p className="text-gray-500">Visual insights into fleet performance and inspection data.</p>
        </div>
        <button
          onClick={refreshReports}
          disabled={refreshing}
          className="flex items-center px-4 py-2 bg-gray-900 text-white rounded-lg hover:bg-gray-800 transition-colors disabled:opacity-50"
        >
          <RefreshCw size={16} className={`mr-2 ${refreshing ? 'animate-spin' : ''}`} />
          Refresh
        </button>
      </div>

      {/* Date Range Filter */}
      <div className="bg-white p-4 rounded-xl shadow-sm border border-gray-100">
        <div className="flex flex-wrap gap-4 items-end">
          <div className="flex flex-col flex-1 min-w-[150px]">
            <label className="text-xs text-gray-500 mb-1 font-medium flex items-center">
              <Calendar size={12} className="mr-1" /> Start Date
            </label>
            <input
              type="date"
              value={startDate}
              onChange={(e) => setStartDate(e.target.value)}
              className="border border-gray-300 rounded-lg px-3 py-2 text-sm bg-white focus:ring-2 focus:ring-blue-500 focus:outline-none"
            />
          </div>
          <div className="flex flex-col flex-1 min-w-[150px]">
            <label className="text-xs text-gray-500 mb-1 font-medium flex items-center">
              <Calendar size={12} className="mr-1" /> End Date
            </label>
            <input
              type="date"
              value={endDate}
              onChange={(e) => setEndDate(e.target.value)}
              className="border border-gray-300 rounded-lg px-3 py-2 text-sm bg-white focus:ring-2 focus:ring-blue-500 focus:outline-none"
            />
          </div>
          <div className="flex gap-2">
            <button
              onClick={() => handleQuickFilter(7)}
              className="px-3 py-2 text-sm border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
            >
              Last 7 Days
            </button>
            <button
              onClick={() => handleQuickFilter(30)}
              className="px-3 py-2 text-sm border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
            >
              Last 30 Days
            </button>
            <button
              onClick={() => handleQuickFilter(90)}
              className="px-3 py-2 text-sm border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
            >
              Last 90 Days
            </button>
          </div>
        </div>
      </div>

      {/* Summary Statistics */}
      {summary && (
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          <div className="bg-white p-4 rounded-xl shadow-sm border border-gray-100">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-xs text-gray-500 uppercase font-semibold">Total Inspections</p>
                <p className="text-2xl font-bold text-gray-800 mt-1">{summary.totalInspections}</p>
                <p className="text-xs text-gray-400 mt-1">
                  Avg {summary.averageDefectsPerInspection} defects/inspection
                </p>
              </div>
              <FileText className="text-blue-400" size={32} />
            </div>
          </div>
          <div className="bg-white p-4 rounded-xl shadow-sm border border-gray-100">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-xs text-gray-500 uppercase font-semibold">Total Defects</p>
                <p className="text-2xl font-bold text-red-600 mt-1">{summary.totalDefects}</p>
                <p className="text-xs text-red-400 mt-1">
                  {summary.unresolvedDefects} unresolved
                </p>
              </div>
              <AlertTriangle className="text-red-400" size={32} />
            </div>
          </div>
          <div className="bg-white p-4 rounded-xl shadow-sm border border-gray-100">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-xs text-blue-500 uppercase font-semibold">Pre-Rental</p>
                <p className="text-2xl font-bold text-blue-600 mt-1">{summary.preRentalInspections}</p>
              </div>
              <TrendingUp className="text-blue-400" size={32} />
            </div>
          </div>
          <div className="bg-white p-4 rounded-xl shadow-sm border border-gray-100">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-xs text-green-500 uppercase font-semibold">Post-Rental</p>
                <p className="text-2xl font-bold text-green-600 mt-1">{summary.postRentalInspections}</p>
              </div>
              <TrendingUp className="text-green-400" size={32} />
            </div>
          </div>
        </div>
      )}

      {/* Charts Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Inspection Trends */}
        <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100 h-80">
          <h3 className="text-lg font-semibold text-gray-800 mb-4">Inspection Trends</h3>
          {inspectionTrends.length > 0 ? (
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={inspectionTrends}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} />
                <XAxis dataKey="date" axisLine={false} tickLine={false} />
                <YAxis axisLine={false} tickLine={false} />
                <Tooltip />
                <Line type="monotone" dataKey="inspectionCount" stroke="#3b82f6" strokeWidth={2} dot={{ r: 3 }} name="Inspections" />
                <Line type="monotone" dataKey="defectCount" stroke="#ef4444" strokeWidth={2} dot={{ r: 3 }} name="Defects" />
              </LineChart>
            </ResponsiveContainer>
          ) : (
            <div className="flex items-center justify-center h-full text-gray-400">
              No data available for selected period
            </div>
          )}
        </div>

        {/* Defect Breakdown */}
        <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100 h-80">
          <h3 className="text-lg font-semibold text-gray-800 mb-4">Defect Breakdown</h3>
          {defectBreakdown.length > 0 ? (
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie
                  data={defectBreakdown}
                  dataKey="count"
                  nameKey="defectType"
                  cx="50%"
                  cy="50%"
                  outerRadius={80}
                  label={(entry) => `${entry.defectType}: ${entry.percentage}%`}
                >
                  {defectBreakdown.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={DEFECT_COLORS[entry.defectType as keyof typeof DEFECT_COLORS] || '#94a3b8'} />
                  ))}
                </Pie>
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
          ) : (
            <div className="flex items-center justify-center h-full text-gray-400">
              No defects recorded for selected period
            </div>
          )}
        </div>

        {/* Vehicle Status Trends */}
        <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100 h-80">
          <h3 className="text-lg font-semibold text-gray-800 mb-4">Vehicle Status Distribution</h3>
          {vehicleStatusTrends.length > 0 ? (
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={vehicleStatusTrends}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} />
                <XAxis dataKey="date" axisLine={false} tickLine={false} />
                <YAxis axisLine={false} tickLine={false} />
                <Tooltip />
                <Bar dataKey="availableCount" fill="#10b981" radius={[4, 4, 0, 0]} name="Available" />
                <Bar dataKey="rentedCount" fill="#3b82f6" radius={[4, 4, 0, 0]} name="Rented" />
                <Bar dataKey="maintenanceCount" fill="#f97316" radius={[4, 4, 0, 0]} name="Maintenance" />
              </BarChart>
            </ResponsiveContainer>
          ) : (
            <div className="flex items-center justify-center h-full text-gray-400">
              No vehicle status data available
            </div>
          )}
        </div>

        {/* Inspection Type Breakdown */}
        {summary && (
          <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100 h-80">
            <h3 className="text-lg font-semibold text-gray-800 mb-4">Inspection Types</h3>
            <ResponsiveContainer width="100%" height="100%">
              <BarChart
                data={[
                  { type: 'Pre-Rental', count: summary.preRentalInspections },
                  { type: 'Post-Rental', count: summary.postRentalInspections },
                  { type: 'Periodic', count: summary.periodicInspections },
                ]}
              >
                <CartesianGrid strokeDasharray="3 3" vertical={false} />
                <XAxis dataKey="type" axisLine={false} tickLine={false} />
                <YAxis axisLine={false} tickLine={false} />
                <Tooltip />
                <Bar dataKey="count" fill="#8b5cf6" radius={[4, 4, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        )}
      </div>
    </div>
  );
};

export default Reports;
