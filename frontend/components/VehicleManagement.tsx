import React, { useState, useEffect } from 'react';
import { Search, Filter, Plus, Car, CheckCircle, Key, PenTool, Eye, Edit2, Trash2 } from 'lucide-react';
import { Vehicle } from '../types';

const MOCK_VEHICLES: Vehicle[] = [
  { id: 'RNT-2025-001', make: 'Toyota', model: 'Camry', year: '2023', plate: 'ABC-1234', status: 'Available', lastService: 'Jan 15, 2025' },
  { id: 'RNT-2025-002', make: 'Honda', model: 'CR-V', year: '2024', plate: 'DEF-5678', status: 'Rented', lastService: 'Dec 20, 2024' },
  { id: 'RNT-2025-003', make: 'Ford', model: 'Transit', year: '2023', plate: 'GHI-9012', status: 'Maintenance', lastService: 'Nov 30, 2024' },
  { id: 'RNT-2025-004', make: 'Tesla', model: 'Model 3', year: '2024', plate: 'JKL-3456', status: 'Available', lastService: 'Jan 10, 2025' },
  { id: 'RNT-2025-005', make: 'Nissan', model: 'Altima', year: '2022', plate: 'MNO-7890', status: 'Rented', lastService: 'Oct 05, 2024' },
];

const VehicleManagement: React.FC = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [vehicles, setVehicles] = useState<Vehicle[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchVehicles = async () => {
      try {
        const response = await fetch('http://localhost:8080/api/vehicles');
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        setVehicles(data);
      } catch (error) {
        setError("Failed to fetch vehicles.");
        console.error("Error fetching vehicles:", error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchVehicles();
  }, []); // Empty dependency array means this effect runs once on mount

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'Available': return 'bg-green-100 text-green-700 border-green-200';
      case 'Rented': return 'bg-gray-100 text-gray-700 border-gray-200';
      case 'Maintenance': return 'bg-red-100 text-red-700 border-red-200';
      default: return 'bg-gray-100 text-gray-700';
    }
  };

  const getIcon = (status: string) => {
     switch (status) {
      case 'Available': return <CheckCircle size={14} className="mr-1"/>;
      case 'Rented': return <Key size={14} className="mr-1"/>;
      case 'Maintenance': return <PenTool size={14} className="mr-1"/>;
      default: return null;
    }
  };

  if (isLoading) {
    return <div className="text-center py-8">Loading vehicles...</div>;
  }

  if (error) {
    return <div className="text-center py-8 text-red-500">Error: {error}</div>;
  }

  const filteredVehicles = vehicles.filter(v => 
    v.make.toLowerCase().includes(searchTerm.toLowerCase()) || 
    v.id.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex justify-between items-end">
        <div>
          <h2 className="text-2xl font-bold text-gray-800">Vehicle Management</h2>
          <p className="text-gray-500">Manage your fleet vehicles, add new vehicles, and edit existing records.</p>
        </div>
        <button className="bg-gray-900 hover:bg-gray-800 text-white px-4 py-2 rounded-lg flex items-center shadow-md transition-colors">
          <Plus size={18} className="mr-2" /> Add Vehicle
        </button>
      </div>

      {/* Stats Strip */}
      <div className="grid grid-cols-4 gap-4">
        <div className="bg-white p-4 rounded-lg shadow-sm border border-gray-100 flex justify-between items-center">
             <div><p className="text-xs text-gray-500">Total Vehicles</p><p className="text-xl font-bold">{vehicles.length}</p></div>
             <Car className="text-gray-300" />
        </div>
        <div className="bg-white p-4 rounded-lg shadow-sm border border-gray-100 flex justify-between items-center">
             <div><p className="text-xs text-gray-500">Available</p><p className="text-xl font-bold">{vehicles.filter(v => v.status === 'Available').length}</p></div>
             <CheckCircle className="text-gray-300" />
        </div>
        <div className="bg-white p-4 rounded-lg shadow-sm border border-gray-100 flex justify-between items-center">
             <div><p className="text-xs text-gray-500">Rented Out</p><p className="text-xl font-bold">{vehicles.filter(v => v.status === 'Rented').length}</p></div>
             <Key className="text-gray-300" />
        </div>
        <div className="bg-white p-4 rounded-lg shadow-sm border border-gray-100 flex justify-between items-center">
             <div><p className="text-xs text-gray-500">Maintenance</p><p className="text-xl font-bold">{vehicles.filter(v => v.status === 'Maintenance').length}</p></div>
             <PenTool className="text-gray-300" />
        </div>
      </div>

      {/* Search & Filter */}
      <div className="bg-white p-4 rounded-xl shadow-sm border border-gray-100 flex flex-col md:flex-row gap-4">
        <div className="flex-1 relative">
          <Search className="absolute left-3 top-2.5 text-gray-400" size={20} />
          <input 
            type="text" 
            placeholder="Search vehicles..." 
            className="w-full pl-10 pr-4 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
        <div className="flex gap-4">
           <select className="border border-gray-200 rounded-lg px-4 py-2 bg-white text-gray-700 focus:outline-none focus:ring-2 focus:ring-blue-500">
             <option>All Status</option>
             <option>Available</option>
             <option>Rented</option>
             <option>Maintenance</option>
           </select>
           <select className="border border-gray-200 rounded-lg px-4 py-2 bg-white text-gray-700 focus:outline-none focus:ring-2 focus:ring-blue-500">
             <option>All Types</option>
             <option>Sedan</option>
             <option>SUV</option>
             <option>Truck</option>
           </select>
           <button className="flex items-center px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200">
             <Filter size={18} className="mr-2" /> Filter
           </button>
        </div>
      </div>

      {/* Table */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-gray-50 border-b border-gray-100 text-xs uppercase text-gray-500 font-semibold tracking-wider">
                <th className="px-6 py-4">Vehicle ID</th>
                <th className="px-6 py-4">Make / Model</th>
                <th className="px-6 py-4">License Plate</th>
                <th className="px-6 py-4">Status</th>
                <th className="px-6 py-4">Last Service</th>
                <th className="px-6 py-4 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {filteredVehicles.map((vehicle) => (
                <tr key={vehicle.id} className="hover:bg-gray-50 transition-colors">
                  <td className="px-6 py-4 font-medium text-gray-800">{vehicle.id}</td>
                  <td className="px-6 py-4 flex items-center gap-3">
                    <div className="w-8 h-8 rounded bg-gray-200 flex items-center justify-center text-gray-500 text-xs font-bold">
                        {vehicle.make.substring(0,3).toUpperCase()}
                    </div>
                    <div>
                      <div className="font-medium text-gray-900">{vehicle.make} {vehicle.model}</div>
                      <div className="text-xs text-gray-500">{vehicle.year}</div>
                    </div>
                  </td>
                  <td className="px-6 py-4 text-gray-600 font-mono">{vehicle.plate}</td>
                  <td className="px-6 py-4">
                    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium border ${getStatusColor(vehicle.status)}`}>
                      {getIcon(vehicle.status)}
                      {vehicle.status}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-gray-600 text-sm">{vehicle.lastService}</td>
                  <td className="px-6 py-4 text-right">
                    <div className="flex justify-end gap-2">
                        <button className="text-gray-400 hover:text-blue-600"><Eye size={18} /></button>
                        <button className="text-gray-400 hover:text-amber-600"><Edit2 size={18} /></button>
                        <button className="text-gray-400 hover:text-red-600"><Trash2 size={18} /></button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        <div className="px-6 py-4 border-t border-gray-100 flex justify-between items-center bg-gray-50">
           <span className="text-sm text-gray-500">Showing 1 to {filteredVehicles.length} of {vehicles.length} results</span>
           <div className="flex gap-1">
             <button className="px-3 py-1 rounded border border-gray-300 bg-white text-gray-600 hover:bg-gray-50 text-sm">Previous</button>
             <button className="px-3 py-1 rounded bg-gray-900 text-white text-sm">1</button>
             <button className="px-3 py-1 rounded border border-gray-300 bg-white text-gray-600 hover:bg-gray-50 text-sm">2</button>
             <button className="px-3 py-1 rounded border border-gray-300 bg-white text-gray-600 hover:bg-gray-50 text-sm">3</button>
             <button className="px-3 py-1 rounded border border-gray-300 bg-white text-gray-600 hover:bg-gray-50 text-sm">Next</button>
           </div>
        </div>
      </div>
    </div>
  );
};

export default VehicleManagement;