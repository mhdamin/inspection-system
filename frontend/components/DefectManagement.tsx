import React, { useState, useEffect } from 'react';
import { AlertTriangle, Search, Filter, Calendar, CheckCircle, Clock, RefreshCw } from 'lucide-react';
import api from '../api';
import { DefectResponse, ChecklistResponse, DefectWithDetails } from '../types';
import DefectDetailModal from './DefectDetailModal';

const DefectManagement: React.FC = () => {
  const [defects, setDefects] = useState<DefectWithDetails[]>([]);
  const [filteredDefects, setFilteredDefects] = useState<DefectWithDetails[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [refreshing, setRefreshing] = useState(false);
  const [selectedDefect, setSelectedDefect] = useState<DefectWithDetails | null>(null);

  // Filter states
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedType, setSelectedType] = useState('');
  const [selectedStatus, setSelectedStatus] = useState('');
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');

  // Pagination
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage] = useState(15);

  useEffect(() => {
    fetchDefects();
  }, []);

  useEffect(() => {
    applyFilters();
  }, [defects, searchQuery, selectedType, selectedStatus, startDate, endDate]);

  const fetchDefects = async () => {
    setIsLoading(true);
    setError(null);
    try {
      // Step 1: Fetch all checklists
      const checklists = await api.get<ChecklistResponse[]>('/api/checklists');

      // Step 2: Fetch defects for each checklist
      const allDefects: DefectWithDetails[] = [];

      for (const checklist of checklists) {
        try {
          const checklistDefects = await api.get<DefectResponse[]>(`/api/checklists/${checklist.id}/defects`);

          // Enhance defects with checklist and vehicle info
          const enhancedDefects: DefectWithDetails[] = checklistDefects.map(defect => ({
            ...defect,
            checklistNumber: checklist.checklistNumber,
            vehiclePlate: checklist.vehicle.plateNumber,
            vehicleInfo: `${checklist.vehicle.manufacturer} ${checklist.vehicle.model} (${checklist.vehicle.year})`,
            customerName: checklist.customerName,
            status: defect.status || 'NEW' // Default to NEW if not set
          }));

          allDefects.push(...enhancedDefects);
        } catch (err) {
          console.error(`Error fetching defects for checklist ${checklist.id}:`, err);
        }
      }

      setDefects(allDefects);
      setFilteredDefects(allDefects);
    } catch (error) {
      console.error('Error fetching defects:', error);
      setError('Failed to load defects. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  const refreshData = async () => {
    setRefreshing(true);
    await fetchDefects();
    setRefreshing(false);
  };

  const applyFilters = () => {
    let filtered = [...defects];

    // Search query (description, vehicle plate, checklist#)
    if (searchQuery) {
      filtered = filtered.filter(d =>
        d.description.toLowerCase().includes(searchQuery.toLowerCase()) ||
        (d.vehiclePlate && d.vehiclePlate.toLowerCase().includes(searchQuery.toLowerCase())) ||
        (d.checklistNumber && d.checklistNumber.toLowerCase().includes(searchQuery.toLowerCase()))
      );
    }

    // Type filter
    if (selectedType && selectedType !== 'ALL') {
      filtered = filtered.filter(d => d.defectType === selectedType);
    }

    // Status filter
    if (selectedStatus && selectedStatus !== 'ALL') {
      filtered = filtered.filter(d => (d.status || 'NEW') === selectedStatus);
    }

    // Date range filter
    if (startDate) {
      filtered = filtered.filter(d => new Date(d.createdAt) >= new Date(startDate));
    }
    if (endDate) {
      filtered = filtered.filter(d => new Date(d.createdAt) <= new Date(endDate + 'T23:59:59'));
    }

    setFilteredDefects(filtered);
    setCurrentPage(1);
  };

  const clearFilters = () => {
    setSearchQuery('');
    setSelectedType('');
    setSelectedStatus('');
    setStartDate('');
    setEndDate('');
    setFilteredDefects(defects);
    setCurrentPage(1);
  };

  const getDefectTypeColor = (type: string) => {
    switch (type) {
      case 'SCRATCH': return 'bg-yellow-100 text-yellow-700 border-yellow-300';
      case 'DENT': return 'bg-orange-100 text-orange-700 border-orange-300';
      case 'CRACK': return 'bg-red-100 text-red-700 border-red-300';
      default: return 'bg-gray-100 text-gray-700 border-gray-300';
    }
  };

  const getStatusColor = (status?: string) => {
    switch (status || 'NEW') {
      case 'NEW': return 'bg-blue-100 text-blue-700';
      case 'IN_PROGRESS': return 'bg-yellow-100 text-yellow-700';
      case 'RESOLVED': return 'bg-green-100 text-green-700';
      default: return 'bg-gray-100 text-gray-700';
    }
  };

  const getStatusIcon = (status?: string) => {
    switch (status || 'NEW') {
      case 'NEW': return <AlertTriangle size={14} />;
      case 'IN_PROGRESS': return <Clock size={14} />;
      case 'RESOLVED': return <CheckCircle size={14} />;
      default: return <AlertTriangle size={14} />;
    }
  };

  // Pagination
  const indexOfLastItem = currentPage * itemsPerPage;
  const indexOfFirstItem = indexOfLastItem - itemsPerPage;
  const currentDefects = filteredDefects.slice(indexOfFirstItem, indexOfLastItem);
  const totalPages = Math.ceil(filteredDefects.length / itemsPerPage);

  const nextPage = () => {
    if (currentPage < totalPages) setCurrentPage(currentPage + 1);
  };

  const prevPage = () => {
    if (currentPage > 1) setCurrentPage(currentPage - 1);
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center py-20">
        <div className="text-center">
          <RefreshCw className="animate-spin mx-auto mb-4 text-gray-400" size={40} />
          <div className="text-gray-500">Loading defects...</div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-red-50 border border-red-200 rounded-xl p-6 text-center">
        <AlertTriangle className="mx-auto mb-4 text-red-500" size={48} />
        <h3 className="text-lg font-semibold text-red-700 mb-2">Error Loading Defects</h3>
        <p className="text-red-600 mb-4">{error}</p>
        <button
          onClick={fetchDefects}
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
          <h2 className="text-2xl font-bold text-gray-800">Defect Management</h2>
          <p className="text-gray-500">Track and manage all vehicle defects across inspections.</p>
        </div>
        <button
          onClick={refreshData}
          disabled={refreshing}
          className="flex items-center px-4 py-2 bg-gray-900 text-white rounded-lg hover:bg-gray-800 transition-colors disabled:opacity-50"
        >
          <RefreshCw size={16} className={`mr-2 ${refreshing ? 'animate-spin' : ''}`} />
          Refresh
        </button>
      </div>

      {/* Statistics */}
      <div className="grid grid-cols-1 md:grid-cols-5 gap-4">
        <div className="bg-white p-4 rounded-xl shadow-sm border border-gray-100">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-xs text-gray-500 uppercase font-semibold">Total Defects</p>
              <p className="text-2xl font-bold text-gray-800 mt-1">{filteredDefects.length}</p>
            </div>
            <AlertTriangle className="text-gray-400" size={32} />
          </div>
        </div>
        <div className="bg-white p-4 rounded-xl shadow-sm border border-gray-100">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-xs text-yellow-500 uppercase font-semibold">Scratches</p>
              <p className="text-2xl font-bold text-yellow-600 mt-1">
                {filteredDefects.filter(d => d.defectType === 'SCRATCH').length}
              </p>
            </div>
            <AlertTriangle className="text-yellow-400" size={32} />
          </div>
        </div>
        <div className="bg-white p-4 rounded-xl shadow-sm border border-gray-100">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-xs text-orange-500 uppercase font-semibold">Dents</p>
              <p className="text-2xl font-bold text-orange-600 mt-1">
                {filteredDefects.filter(d => d.defectType === 'DENT').length}
              </p>
            </div>
            <AlertTriangle className="text-orange-400" size={32} />
          </div>
        </div>
        <div className="bg-white p-4 rounded-xl shadow-sm border border-gray-100">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-xs text-red-500 uppercase font-semibold">Cracks</p>
              <p className="text-2xl font-bold text-red-600 mt-1">
                {filteredDefects.filter(d => d.defectType === 'CRACK').length}
              </p>
            </div>
            <AlertTriangle className="text-red-400" size={32} />
          </div>
        </div>
        <div className="bg-white p-4 rounded-xl shadow-sm border border-gray-100">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-xs text-green-500 uppercase font-semibold">Resolved</p>
              <p className="text-2xl font-bold text-green-600 mt-1">
                {filteredDefects.filter(d => (d.status || 'NEW') === 'RESOLVED').length}
              </p>
            </div>
            <CheckCircle className="text-green-400" size={32} />
          </div>
        </div>
      </div>

      {/* Search and Filters */}
      <div className="bg-white p-4 rounded-xl shadow-sm border border-gray-100">
        <div className="flex flex-col gap-4">
          {/* Search Bar */}
          <div className="flex gap-2">
            <div className="flex-1 relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={18} />
              <input
                type="text"
                value={searchQuery}
                onChange={e => setSearchQuery(e.target.value)}
                placeholder="Search by description, vehicle plate, or checklist #..."
                className="w-full pl-10 pr-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:outline-none"
              />
            </div>
            <button
              onClick={clearFilters}
              className="flex items-center px-4 py-2 text-gray-600 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
            >
              <Filter size={16} className="mr-2" />
              Clear Filters
            </button>
          </div>

          {/* Advanced Filters */}
          <div className="grid grid-cols-1 md:grid-cols-4 gap-3">
            <div className="flex flex-col">
              <label className="text-xs text-gray-500 mb-1 font-medium">Defect Type</label>
              <select
                value={selectedType}
                onChange={e => setSelectedType(e.target.value)}
                className="border border-gray-300 rounded-lg px-3 py-2 text-sm bg-white focus:ring-2 focus:ring-blue-500 focus:outline-none"
              >
                <option value="">All Types</option>
                <option value="SCRATCH">Scratch</option>
                <option value="DENT">Dent</option>
                <option value="CRACK">Crack</option>
              </select>
            </div>
            <div className="flex flex-col">
              <label className="text-xs text-gray-500 mb-1 font-medium">Status</label>
              <select
                value={selectedStatus}
                onChange={e => setSelectedStatus(e.target.value)}
                className="border border-gray-300 rounded-lg px-3 py-2 text-sm bg-white focus:ring-2 focus:ring-blue-500 focus:outline-none"
              >
                <option value="">All Status</option>
                <option value="NEW">New</option>
                <option value="IN_PROGRESS">In Progress</option>
                <option value="RESOLVED">Resolved</option>
              </select>
            </div>
            <div className="flex flex-col">
              <label className="text-xs text-gray-500 mb-1 font-medium flex items-center">
                <Calendar size={12} className="mr-1" /> Start Date
              </label>
              <input
                type="date"
                value={startDate}
                onChange={e => setStartDate(e.target.value)}
                className="border border-gray-300 rounded-lg px-3 py-2 text-sm bg-white focus:ring-2 focus:ring-blue-500 focus:outline-none"
              />
            </div>
            <div className="flex flex-col">
              <label className="text-xs text-gray-500 mb-1 font-medium flex items-center">
                <Calendar size={12} className="mr-1" /> End Date
              </label>
              <input
                type="date"
                value={endDate}
                onChange={e => setEndDate(e.target.value)}
                className="border border-gray-300 rounded-lg px-3 py-2 text-sm bg-white focus:ring-2 focus:ring-blue-500 focus:outline-none"
              />
            </div>
          </div>
        </div>
      </div>

      {/* Defects Table */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
        {currentDefects.length === 0 ? (
          <div className="p-12 text-center">
            <CheckCircle className="mx-auto mb-4 text-gray-300" size={64} />
            <h3 className="text-lg font-semibold text-gray-600 mb-2">No Defects Found</h3>
            <p className="text-gray-500">
              {defects.length === 0
                ? "No defects have been reported yet. Great job keeping vehicles in top condition!"
                : "No defects match your search criteria. Try adjusting your filters."}
            </p>
          </div>
        ) : (
          <>
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead className="bg-gray-50 border-b border-gray-200">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                      ID
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                      Type
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                      Description
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                      Vehicle
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                      Checklist
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                      Status
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                      Date
                    </th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-200">
                  {currentDefects.map((defect) => (
                    <tr
                      key={defect.defectId}
                      onClick={() => setSelectedDefect(defect)}
                      className="hover:bg-blue-50 cursor-pointer transition-colors"
                    >
                      <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                        #{defect.defectId}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <span className={`px-2 py-1 text-xs font-semibold rounded border ${getDefectTypeColor(defect.defectType)}`}>
                          {defect.defectType}
                        </span>
                      </td>
                      <td className="px-6 py-4">
                        <div className="text-sm text-gray-900 max-w-xs truncate" title={defect.description}>
                          {defect.description}
                        </div>
                        {(defect.diagramX !== undefined && defect.diagramY !== undefined) && (
                          <div className="text-xs text-gray-500 mt-1">
                            Location: ({defect.diagramX}%, {defect.diagramY}%)
                          </div>
                        )}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="text-sm font-medium text-gray-900">{defect.vehiclePlate}</div>
                        <div className="text-xs text-gray-500">{defect.vehicleInfo}</div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700">
                        {defect.checklistNumber}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <span className={`px-2 py-1 text-xs font-semibold rounded-full flex items-center gap-1 w-fit ${getStatusColor(defect.status)}`}>
                          {getStatusIcon(defect.status)}
                          {defect.status || 'NEW'}
                        </span>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700">
                        {new Date(defect.createdAt).toLocaleDateString()}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            {/* Pagination */}
            {totalPages > 1 && (
              <div className="px-6 py-4 border-t border-gray-200 flex items-center justify-between">
                <div className="text-sm text-gray-600">
                  Showing {indexOfFirstItem + 1} to {Math.min(indexOfLastItem, filteredDefects.length)} of {filteredDefects.length} defects
                </div>
                <div className="flex gap-2">
                  <button
                    onClick={prevPage}
                    disabled={currentPage === 1}
                    className="px-4 py-2 text-sm border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    Previous
                  </button>
                  <div className="px-4 py-2 text-sm text-gray-700">
                    Page {currentPage} of {totalPages}
                  </div>
                  <button
                    onClick={nextPage}
                    disabled={currentPage === totalPages}
                    className="px-4 py-2 text-sm border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    Next
                  </button>
                </div>
              </div>
            )}
          </>
        )}
      </div>

      {/* Defect Detail Modal */}
      {selectedDefect && (
        <DefectDetailModal
          defect={selectedDefect}
          onClose={() => setSelectedDefect(null)}
        />
      )}
    </div>
  );
};

export default DefectManagement;
