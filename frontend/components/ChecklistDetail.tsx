import React, { useState, useEffect } from 'react';
import { FileText, Calendar, User, Car, Phone, AlertCircle, CheckCircle, Clock, Printer } from 'lucide-react';
import api from '../api';
import { ChecklistResponse, SubChecklistResponse, DefectResponse } from '../types';
import ImageGallery from './ImageGallery';

interface ChecklistDetailProps {
  checklistId: string;
}

const ChecklistDetail: React.FC<ChecklistDetailProps> = ({ checklistId }) => {
  const [checklist, setChecklist] = useState<ChecklistResponse | null>(null);
  const [subChecklists, setSubChecklists] = useState<SubChecklistResponse[]>([]);
  const [defects, setDefects] = useState<DefectResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetchChecklistData();
  }, [checklistId]);

  const fetchChecklistData = async () => {
    setIsLoading(true);
    setError(null);
    try {
      // Fetch checklist details
      const checklistData = await api.get<ChecklistResponse>(`/api/checklists/${checklistId}`);
      setChecklist(checklistData);

      // Fetch sub-checklists
      const subChecklistsData = await api.get<SubChecklistResponse[]>(`/api/sub-checklists/checklist/${checklistId}`);
      setSubChecklists(subChecklistsData);

      // Fetch defects
      const defectsData = await api.get<DefectResponse[]>(`/api/checklists/${checklistId}/defects`);
      setDefects(defectsData);
    } catch (error) {
      console.error('Error fetching checklist data:', error);
      setError('Failed to load checklist details. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  const handlePrint = () => {
    window.print();
  };

  const getTypeColor = (type: string) => {
    switch (type) {
      case 'Pre-Rental': return 'bg-blue-100 text-blue-700 border-blue-300';
      case 'Post-Rental': return 'bg-green-100 text-green-700 border-green-300';
      case 'Periodic': return 'bg-purple-100 text-purple-700 border-purple-300';
      default: return 'bg-gray-100 text-gray-700 border-gray-300';
    }
  };

  const getDefectSeverityColor = (severity?: string) => {
    switch (severity) {
      case 'CRITICAL': return 'bg-red-100 text-red-700 border-red-300';
      case 'MAJOR': return 'bg-orange-100 text-orange-700 border-orange-300';
      case 'MINOR': return 'bg-yellow-100 text-yellow-700 border-yellow-300';
      default: return 'bg-gray-100 text-gray-700 border-gray-300';
    }
  };

  const getSubChecklistIcon = (type: string) => {
    switch (type) {
      case 'EXTERIOR': return <Car size={20} className="text-blue-500" />;
      case 'INTERIOR': return <FileText size={20} className="text-green-500" />;
      case 'TYRES': return <AlertCircle size={20} className="text-orange-500" />;
      case 'FLUIDS': return <AlertCircle size={20} className="text-purple-500" />;
      case 'ELECTRONICS': return <AlertCircle size={20} className="text-yellow-500" />;
      case 'SAFETY_EQUIPMENT': return <CheckCircle size={20} className="text-red-500" />;
      default: return <FileText size={20} className="text-gray-500" />;
    }
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center py-20">
        <div className="text-center">
          <Clock className="animate-spin mx-auto mb-4 text-gray-400" size={40} />
          <div className="text-gray-500">Loading inspection details...</div>
        </div>
      </div>
    );
  }

  if (error || !checklist) {
    return (
      <div className="bg-red-50 border border-red-200 rounded-xl p-6 text-center">
        <AlertCircle className="mx-auto mb-4 text-red-500" size={48} />
        <h3 className="text-lg font-semibold text-red-700 mb-2">Error Loading Details</h3>
        <p className="text-red-600">{error || 'Checklist not found'}</p>
      </div>
    );
  }

  return (
    <div className="space-y-6 print:space-y-4">
      {/* Header */}
      <div className="flex justify-between items-start print:hidden">
        <div>
          <h2 className="text-2xl font-bold text-gray-800">Inspection Details</h2>
          <p className="text-gray-500">Checklist #{checklist.checklistNumber}</p>
        </div>
        <button
          onClick={handlePrint}
          className="flex items-center px-4 py-2 bg-gray-900 text-white rounded-lg hover:bg-gray-800 transition-colors"
        >
          <Printer size={16} className="mr-2" />
          Print
        </button>
      </div>

      {/* Print Header */}
      <div className="hidden print:block">
        <h1 className="text-2xl font-bold text-center mb-4">Vehicle Inspection Report</h1>
        <p className="text-center text-gray-600">Checklist #{checklist.checklistNumber}</p>
      </div>

      {/* Overview Card */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
        <div className="flex items-center justify-between mb-4">
          <h3 className="text-lg font-semibold text-gray-800">Inspection Overview</h3>
          <span className={`px-3 py-1 text-sm font-semibold rounded-full border ${getTypeColor(checklist.rentalType)}`}>
            {checklist.rentalType}
          </span>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {/* Left Column */}
          <div className="space-y-4">
            <div>
              <label className="flex items-center text-xs text-gray-500 uppercase font-semibold mb-1">
                <Calendar size={14} className="mr-1" /> Rental Start Date
              </label>
              <p className="text-gray-900 font-medium">
                {new Date(checklist.rentalStartDate).toLocaleDateString()}
              </p>
            </div>

            {checklist.rentalEndDate && (
              <div>
                <label className="flex items-center text-xs text-gray-500 uppercase font-semibold mb-1">
                  <Calendar size={14} className="mr-1" /> Rental End Date
                </label>
                <p className="text-gray-900 font-medium">
                  {new Date(checklist.rentalEndDate).toLocaleDateString()}
                </p>
              </div>
            )}

            <div>
              <label className="flex items-center text-xs text-gray-500 uppercase font-semibold mb-1">
                <User size={14} className="mr-1" /> Customer Name
              </label>
              <p className="text-gray-900 font-medium">{checklist.customerName}</p>
            </div>

            <div>
              <label className="flex items-center text-xs text-gray-500 uppercase font-semibold mb-1">
                <Phone size={14} className="mr-1" /> Phone Number
              </label>
              <p className="text-gray-900 font-medium">{checklist.customerPhone}</p>
            </div>
          </div>

          {/* Right Column */}
          <div className="space-y-4">
            <div>
              <label className="flex items-center text-xs text-gray-500 uppercase font-semibold mb-1">
                <Car size={14} className="mr-1" /> Vehicle
              </label>
              <p className="text-gray-900 font-medium text-lg">{checklist.vehicle.plateNumber}</p>
              <p className="text-gray-600 text-sm">
                {checklist.vehicle.manufacturer} {checklist.vehicle.model} ({checklist.vehicle.year})
              </p>
            </div>

            <div>
              <label className="flex items-center text-xs text-gray-500 uppercase font-semibold mb-1">
                <User size={14} className="mr-1" /> Inspector/Staff
              </label>
              <p className="text-gray-900 font-medium">{checklist.staffName}</p>
            </div>

            <div>
              <label className="flex items-center text-xs text-gray-500 uppercase font-semibold mb-1">
                <Clock size={14} className="mr-1" /> Created At
              </label>
              <p className="text-gray-900 font-medium">
                {new Date(checklist.createdAt).toLocaleString()}
              </p>
            </div>
          </div>
        </div>
      </div>

      {/* Sub-Checklists */}
      {subChecklists.length > 0 && (
        <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
          <h3 className="text-lg font-semibold text-gray-800 mb-4">Inspection Sections</h3>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {subChecklists.map((subChecklist) => (
              <div
                key={subChecklist.id}
                className="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow"
              >
                <div className="flex items-start gap-3">
                  <div className="mt-1">
                    {getSubChecklistIcon(subChecklist.type)}
                  </div>
                  <div className="flex-1">
                    <h4 className="font-semibold text-gray-800 mb-1">
                      {subChecklist.type.replace('_', ' ')}
                    </h4>
                    {subChecklist.remarks && (
                      <p className="text-sm text-gray-600 mt-2">
                        <span className="font-medium">Remarks:</span> {subChecklist.remarks}
                      </p>
                    )}
                    <p className="text-xs text-gray-500 mt-2">
                      Completed: {new Date(subChecklist.createdAt).toLocaleString()}
                    </p>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Defects */}
      {defects.length > 0 ? (
        <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-lg font-semibold text-gray-800">Defects Found</h3>
            <span className="px-3 py-1 text-sm font-semibold bg-red-100 text-red-700 rounded-full">
              {defects.length} {defects.length === 1 ? 'Defect' : 'Defects'}
            </span>
          </div>

          <div className="space-y-4">
            {defects.map((defect) => (
              <div
                key={defect.defectId}
                className="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow"
              >
                <div className="flex items-start justify-between">
                  <div className="flex-1">
                    <div className="flex items-center gap-2 mb-2">
                      <span className={`px-2 py-1 text-xs font-semibold rounded border ${defect.severity ? getDefectSeverityColor(defect.severity) : 'bg-gray-100 text-gray-700 border-gray-300'}`}>
                        {defect.defectType}
                      </span>
                      {defect.severity && (
                        <span className={`px-2 py-1 text-xs font-semibold rounded border ${getDefectSeverityColor(defect.severity)}`}>
                          {defect.severity}
                        </span>
                      )}
                    </div>
                    <p className="text-gray-800 font-medium mb-2">{defect.description}</p>
                    {(defect.diagramX !== undefined && defect.diagramY !== undefined) && (
                      <p className="text-xs text-gray-500">
                        Location: ({defect.diagramX}%, {defect.diagramY}%)
                      </p>
                    )}
                    <p className="text-xs text-gray-500 mt-1">
                      Reported: {new Date(defect.createdAt).toLocaleString()}
                    </p>
                  </div>
                  <AlertCircle className="text-red-500 flex-shrink-0" size={24} />
                </div>

                {/* Defect Images */}
                <div className="mt-4 pt-4 border-t border-gray-200">
                  <h4 className="text-sm font-semibold text-gray-700 mb-3">Defect Images</h4>
                  <ImageGallery defectId={defect.defectId} />
                </div>
              </div>
            ))}
          </div>
        </div>
      ) : (
        <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
          <div className="text-center py-6">
            <CheckCircle className="mx-auto mb-3 text-green-500" size={48} />
            <h3 className="text-lg font-semibold text-gray-800 mb-1">No Defects Found</h3>
            <p className="text-gray-600">This vehicle passed inspection with no issues reported.</p>
          </div>
        </div>
      )}

      {/* Footer - Print Only */}
      <div className="hidden print:block mt-8 pt-4 border-t border-gray-300">
        <p className="text-center text-sm text-gray-600">
          Generated with FleetGuard Inspection System - {new Date().toLocaleString()}
        </p>
      </div>
    </div>
  );
};

export default ChecklistDetail;
