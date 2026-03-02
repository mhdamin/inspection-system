import React, { useState, useEffect } from 'react';
import { Search, Filter, Calendar, User, FileText, RefreshCw, Eye, Download, X } from 'lucide-react';
import api from '../api';
import { ChecklistResponse } from '../types';
import ChecklistDetail from './ChecklistDetail';

const ChecklistHistory: React.FC = () => {
  const [checklists, setChecklists] = useState<ChecklistResponse[]>([]);
  const [filteredChecklists, setFilteredChecklists] = useState<ChecklistResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [refreshing, setRefreshing] = useState(false);
  const [selectedChecklist, setSelectedChecklist] = useState<ChecklistResponse | null>(null);

  // Filter states
  const [searchQuery, setSearchQuery] = useState('');
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [selectedType, setSelectedType] = useState('');
  const [plateFilter, setPlateFilter] = useState('');
  const [customerFilter, setCustomerFilter] = useState('');
  const [staffFilter, setStaffFilter] = useState('');

  // Pagination
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage] = useState(10);

  useEffect(() => {
    fetchChecklists();
  }, []);

  useEffect(() => {
    applyFilters();
  }, [checklists, searchQuery, startDate, endDate, selectedType, plateFilter, customerFilter, staffFilter]);

  const fetchChecklists = async () => {
    setIsLoading(true);
    setError(null);
    try {
      const data = await api.get<ChecklistResponse[]>('/api/checklists');
      setChecklists(data);
      setFilteredChecklists(data);
    } catch (error) {
      setError("Failed to fetch checklists. Please check your connection.");
      console.error("Error fetching checklists:", error);
    } finally {
      setIsLoading(false);
    }
  };

  const refreshData = async () => {
    setRefreshing(true);
    await fetchChecklists();
    setRefreshing(false);
  };

  const applyFilters = () => {
    let filtered = [...checklists];

    // Search query (searches across checklist number, customer name, plate)
    if (searchQuery) {
      filtered = filtered.filter(c =>
        c.checklistNumber.toLowerCase().includes(searchQuery.toLowerCase()) ||
        c.customerName.toLowerCase().includes(searchQuery.toLowerCase()) ||
        c.vehicle.plateNumber.toLowerCase().includes(searchQuery.toLowerCase())
      );
    }

    // Date range filter
    if (startDate) {
      filtered = filtered.filter(c => new Date(c.rentalStartDate) >= new Date(startDate));
    }
    if (endDate) {
      filtered = filtered.filter(c => new Date(c.rentalStartDate) <= new Date(endDate + 'T23:59:59'));
    }

    // Type filter
    if (selectedType && selectedType !== 'ALL') {
      filtered = filtered.filter(c => c.rentalType === selectedType);
    }

    // Plate filter
    if (plateFilter) {
      filtered = filtered.filter(c =>
        c.vehicle.plateNumber.toLowerCase().includes(plateFilter.toLowerCase())
      );
    }

    // Customer filter
    if (customerFilter) {
      filtered = filtered.filter(c =>
        c.customerName.toLowerCase().includes(customerFilter.toLowerCase())
      );
    }

    // Staff filter
    if (staffFilter) {
      filtered = filtered.filter(c =>
        c.staffName.toLowerCase().includes(staffFilter.toLowerCase())
      );
    }

    setFilteredChecklists(filtered);
    setCurrentPage(1); // Reset to first page when filters change
  };

  const clearFilters = () => {
    setSearchQuery('');
    setStartDate('');
    setEndDate('');
    setSelectedType('');
    setPlateFilter('');
    setCustomerFilter('');
    setStaffFilter('');
    setFilteredChecklists(checklists);
    setCurrentPage(1);
  };

  const exportToCSV = () => {
    const headers = ['Checklist #', 'Date', 'Type', 'Vehicle', 'Customer', 'Phone', 'Staff', 'Created'];
    const csvData = filteredChecklists.map(c => [
      c.checklistNumber,
      new Date(c.rentalStartDate).toLocaleDateString(),
      c.rentalType,
      `${c.vehicle.plateNumber} (${c.vehicle.manufacturer} ${c.vehicle.model})`,
      c.customerName,
      c.customerPhone,
      c.staffName,
      new Date(c.createdAt).toLocaleString()
    ]);

    const csvContent = [
      headers.join(','),
      ...csvData.map(row => row.map(cell => `"${cell}"`).join(','))
    ].join('\n');

    const blob = new Blob([csvContent], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `checklists-${new Date().toISOString().split('T')[0]}.csv`;
    a.click();
    window.URL.revokeObjectURL(url);
  };

  const getTypeColor = (type: string) => {
    switch (type) {
      case 'Pre-Rental': return 'bg-blue-100 text-blue-700';
      case 'Post-Rental': return 'bg-green-100 text-green-700';
      case 'Periodic': return 'bg-purple-100 text-purple-700';
      default: return 'bg-gray-100 text-gray-700';
    }
  };

  // Pagination
  const indexOfLastItem = currentPage * itemsPerPage;
  const indexOfFirstItem = indexOfLastItem - itemsPerPage;
  const currentChecklists = filteredChecklists.slice(indexOfFirstItem, indexOfLastItem);
  const totalPages = Math.ceil(filteredChecklists.length / itemsPerPage);

  const nextPage = () => {
    if (currentPage < totalPages) setCurrentPage(currentPage + 1);
  };

  const prevPage = () => {
    if (currentPage > 1) setCurrentPage(currentPage - 1);
  };

  // If a checklist is selected, show detail view
  if (selectedChecklist) {
    return (
      <div>
        <button
          onClick={() => setSelectedChecklist(null)}
          className="mb-4 flex items-center px-4 py-2 text-gray-700 hover:text-gray-900 transition-colors"
        >
          <X size={20} className="mr-2" />
          Back to List
        </button>
        <ChecklistDetail checklistId={selectedChecklist.id} />
      </div>
    );
  }

  if (isLoading) {
    return (
      <div className="flex items-center justify-center py-20">
        <div className="text-center">
          <RefreshCw className="animate-spin mx-auto mb-4 text-gray-400" size={40} />
          <div className="text-gray-500">Loading inspection history...</div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-red-50 border border-red-200 rounded-xl p-6 text-center">
        <FileText className="mx-auto mb-4 text-red-500" size={48} />
        <h3 className="text-lg font-semibold text-red-700 mb-2">Error Loading Checklists</h3>
        <p className="text-red-600 mb-4">{error}</p>
        <button
          onClick={fetchChecklists}
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
          <h2 className="text-2xl font-bold text-gray-800">Inspection History</h2>
          <p className="text-gray-500">View and search all completed vehicle inspections.</p>
        </div>
        <div className="flex gap-2">
          <button
            onClick={exportToCSV}
            className="flex items-center px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors"
          >
            <Download size={16} className="mr-2" />
            Export CSV
          </button>
          <button
            onClick={refreshData}
            disabled={refreshing}
            className="flex items-center px-4 py-2 bg-gray-900 text-white rounded-lg hover:bg-gray-800 transition-colors disabled:opacity-50"
          >
            <RefreshCw size={16} className={`mr-2 ${refreshing ? 'animate-spin' : ''}`} />
            Refresh
          </button>
        </div>
      </div>

      {/* Statistics */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div className="bg-white p-4 rounded-xl shadow-sm border border-gray-100">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-xs text-gray-500 uppercase font-semibold">Total Inspections</p>
              <p className="text-2xl font-bold text-gray-800 mt-1">{filteredChecklists.length}</p>
            </div>
            <FileText className="text-gray-400" size={32} />
          </div>
        </div>
        <div className="bg-white p-4 rounded-xl shadow-sm border border-gray-100">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-xs text-blue-500 uppercase font-semibold">Pre-Rental</p>
              <p className="text-2xl font-bold text-blue-600 mt-1">
                {filteredChecklists.filter(c => c.rentalType === 'Pre-Rental').length}
              </p>
            </div>
            <Calendar className="text-blue-400" size={32} />
          </div>
        </div>
        <div className="bg-white p-4 rounded-xl shadow-sm border border-gray-100">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-xs text-green-500 uppercase font-semibold">Post-Rental</p>
              <p className="text-2xl font-bold text-green-600 mt-1">
                {filteredChecklists.filter(c => c.rentalType === 'Post-Rental').length}
              </p>
            </div>
            <Calendar className="text-green-400" size={32} />
          </div>
        </div>
        <div className="bg-white p-4 rounded-xl shadow-sm border border-gray-100">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-xs text-purple-500 uppercase font-semibold">Periodic</p>
              <p className="text-2xl font-bold text-purple-600 mt-1">
                {filteredChecklists.filter(c => c.rentalType === 'Periodic').length}
              </p>
            </div>
            <Calendar className="text-purple-400" size={32} />
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
                placeholder="Search by checklist #, customer, or plate number..."
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
            <div className="flex flex-col">
              <label className="text-xs text-gray-500 mb-1 font-medium">Inspection Type</label>
              <select
                value={selectedType}
                onChange={e => setSelectedType(e.target.value)}
                className="border border-gray-300 rounded-lg px-3 py-2 text-sm bg-white focus:ring-2 focus:ring-blue-500 focus:outline-none"
              >
                <option value="">All Types</option>
                <option value="Pre-Rental">Pre-Rental</option>
                <option value="Post-Rental">Post-Rental</option>
                <option value="Periodic">Periodic</option>
              </select>
            </div>
            <div className="flex flex-col">
              <label className="text-xs text-gray-500 mb-1 font-medium flex items-center">
                <User size={12} className="mr-1" /> Staff Name
              </label>
              <input
                type="text"
                value={staffFilter}
                onChange={e => setStaffFilter(e.target.value)}
                placeholder="Filter by staff..."
                className="border border-gray-300 rounded-lg px-3 py-2 text-sm bg-white focus:ring-2 focus:ring-blue-500 focus:outline-none"
              />
            </div>
          </div>
        </div>
      </div>

      {/* Checklists Table */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
        {currentChecklists.length === 0 ? (
          <div className="p-12 text-center">
            <FileText className="mx-auto mb-4 text-gray-300" size={64} />
            <h3 className="text-lg font-semibold text-gray-600 mb-2">No Inspections Found</h3>
            <p className="text-gray-500">
              {checklists.length === 0
                ? "No inspection checklists have been submitted yet."
                : "No checklists match your search criteria. Try adjusting your filters."}
            </p>
          </div>
        ) : (
          <>
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead className="bg-gray-50 border-b border-gray-200">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                      Checklist #
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                      Date
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                      Type
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                      Vehicle
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                      Customer
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                      Staff
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                      Actions
                    </th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-200">
                  {currentChecklists.map((checklist) => (
                    <tr key={checklist.id} className="hover:bg-gray-50 transition-colors">
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="text-sm font-medium text-gray-900">{checklist.checklistNumber}</div>
                        <div className="text-xs text-gray-500">
                          {new Date(checklist.createdAt).toLocaleString()}
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700">
                        {new Date(checklist.rentalStartDate).toLocaleDateString()}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <span className={`px-2 py-1 text-xs font-semibold rounded-full ${getTypeColor(checklist.rentalType)}`}>
                          {checklist.rentalType}
                        </span>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="text-sm font-medium text-gray-900">{checklist.vehicle.plateNumber}</div>
                        <div className="text-xs text-gray-500">
                          {checklist.vehicle.manufacturer} {checklist.vehicle.model} ({checklist.vehicle.year})
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="text-sm text-gray-900">{checklist.customerName}</div>
                        <div className="text-xs text-gray-500">{checklist.customerPhone}</div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="flex items-center">
                          <div className="h-8 w-8 rounded-full bg-gradient-to-br from-blue-400 to-blue-600 flex items-center justify-center text-white text-xs font-semibold mr-2">
                            {checklist.staffName.split(' ').map(n => n[0]).join('').toUpperCase()}
                          </div>
                          <span className="text-sm text-gray-700">{checklist.staffName}</span>
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <button
                          onClick={() => setSelectedChecklist(checklist)}
                          className="flex items-center px-3 py-1 text-sm bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
                        >
                          <Eye size={14} className="mr-1" />
                          View
                        </button>
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
                  Showing {indexOfFirstItem + 1} to {Math.min(indexOfLastItem, filteredChecklists.length)} of {filteredChecklists.length} inspections
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
    </div>
  );
};

export default ChecklistHistory;
