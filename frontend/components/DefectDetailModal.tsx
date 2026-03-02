import React, { useState } from 'react';
import { X, AlertTriangle, MapPin, Calendar, FileText } from 'lucide-react';
import { DefectWithDetails } from '../types';
import ImageGallery from './ImageGallery';
import ImageUpload from './ImageUpload';

interface DefectDetailModalProps {
  defect: DefectWithDetails;
  onClose: () => void;
}

const DefectDetailModal: React.FC<DefectDetailModalProps> = ({ defect, onClose }) => {
  const [refreshTrigger, setRefreshTrigger] = useState(0);
  const [activeTab, setActiveTab] = useState<'details' | 'images'>('details');

  const getDefectTypeColor = (type: string) => {
    switch (type) {
      case 'SCRATCH': return 'bg-yellow-100 text-yellow-700 border-yellow-300';
      case 'DENT': return 'bg-orange-100 text-orange-700 border-orange-300';
      case 'CRACK': return 'bg-red-100 text-red-700 border-red-300';
      default: return 'bg-gray-100 text-gray-700 border-gray-300';
    }
  };

  const getStatusColor = (status?: string) => {
    switch (status) {
      case 'NEW': return 'bg-blue-100 text-blue-700';
      case 'IN_PROGRESS': return 'bg-yellow-100 text-yellow-700';
      case 'RESOLVED': return 'bg-green-100 text-green-700';
      default: return 'bg-gray-100 text-gray-700';
    }
  };

  const handleUploadSuccess = () => {
    // Trigger refresh of ImageGallery
    setRefreshTrigger(prev => prev + 1);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black bg-opacity-50">
      <div className="bg-white rounded-xl shadow-2xl max-w-4xl w-full max-h-[90vh] overflow-hidden flex flex-col">
        {/* Header */}
        <div className="flex items-center justify-between p-6 border-b border-gray-200">
          <div className="flex items-center gap-3">
            <div className="p-2 bg-red-100 rounded-lg">
              <AlertTriangle className="text-red-600" size={24} />
            </div>
            <div>
              <h2 className="text-xl font-bold text-gray-900">Defect Details</h2>
              <p className="text-sm text-gray-500">Defect ID: #{defect.defectId}</p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-2 text-gray-400 hover:text-gray-600 hover:bg-gray-100 rounded-lg transition-colors"
          >
            <X size={24} />
          </button>
        </div>

        {/* Tabs */}
        <div className="flex border-b border-gray-200 px-6">
          <button
            onClick={() => setActiveTab('details')}
            className={`px-4 py-3 font-medium text-sm border-b-2 transition-colors ${
              activeTab === 'details'
                ? 'border-blue-600 text-blue-600'
                : 'border-transparent text-gray-500 hover:text-gray-700'
            }`}
          >
            <div className="flex items-center gap-2">
              <FileText size={16} />
              Details
            </div>
          </button>
          <button
            onClick={() => setActiveTab('images')}
            className={`px-4 py-3 font-medium text-sm border-b-2 transition-colors ${
              activeTab === 'images'
                ? 'border-blue-600 text-blue-600'
                : 'border-transparent text-gray-500 hover:text-gray-700'
            }`}
          >
            <div className="flex items-center gap-2">
              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
              </svg>
              Images
            </div>
          </button>
        </div>

        {/* Content */}
        <div className="flex-1 overflow-y-auto p-6">
          {activeTab === 'details' ? (
            <div className="space-y-6">
              {/* Type and Status */}
              <div className="flex gap-3">
                <span className={`px-3 py-1.5 text-sm font-semibold rounded-lg border ${getDefectTypeColor(defect.defectType)}`}>
                  {defect.defectType}
                </span>
                <span className={`px-3 py-1.5 text-sm font-semibold rounded-lg ${getStatusColor(defect.status || 'NEW')}`}>
                  {defect.status || 'NEW'}
                </span>
              </div>

              {/* Description */}
              <div>
                <label className="block text-xs text-gray-500 uppercase font-semibold mb-2">
                  Description
                </label>
                <p className="text-gray-900 bg-gray-50 p-4 rounded-lg border border-gray-200">
                  {defect.description}
                </p>
              </div>

              {/* Location */}
              {(defect.diagramX !== undefined && defect.diagramY !== undefined) && (
                <div>
                  <label className="flex items-center text-xs text-gray-500 uppercase font-semibold mb-2">
                    <MapPin size={14} className="mr-1" /> Location on Diagram
                  </label>
                  <div className="flex gap-4">
                    <div className="flex-1 bg-gray-50 p-3 rounded-lg border border-gray-200">
                      <span className="text-xs text-gray-500">X Position</span>
                      <p className="text-lg font-semibold text-gray-900">{defect.diagramX}%</p>
                    </div>
                    <div className="flex-1 bg-gray-50 p-3 rounded-lg border border-gray-200">
                      <span className="text-xs text-gray-500">Y Position</span>
                      <p className="text-lg font-semibold text-gray-900">{defect.diagramY}%</p>
                    </div>
                  </div>
                </div>
              )}

              {/* Vehicle Info */}
              {defect.vehiclePlate && (
                <div>
                  <label className="block text-xs text-gray-500 uppercase font-semibold mb-2">
                    Vehicle Information
                  </label>
                  <div className="bg-gray-50 p-4 rounded-lg border border-gray-200">
                    <p className="font-semibold text-gray-900">{defect.vehiclePlate}</p>
                    <p className="text-sm text-gray-600">{defect.vehicleInfo}</p>
                  </div>
                </div>
              )}

              {/* Checklist Info */}
              <div className="grid grid-cols-2 gap-4">
                {defect.checklistNumber && (
                  <div>
                    <label className="block text-xs text-gray-500 uppercase font-semibold mb-2">
                      Checklist Number
                    </label>
                    <p className="text-gray-900 font-medium">{defect.checklistNumber}</p>
                  </div>
                )}
                {defect.customerName && (
                  <div>
                    <label className="block text-xs text-gray-500 uppercase font-semibold mb-2">
                      Customer Name
                    </label>
                    <p className="text-gray-900 font-medium">{defect.customerName}</p>
                  </div>
                )}
              </div>

              {/* Timestamps */}
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="flex items-center text-xs text-gray-500 uppercase font-semibold mb-2">
                    <Calendar size={14} className="mr-1" /> Created At
                  </label>
                  <p className="text-gray-900 text-sm">
                    {new Date(defect.createdAt).toLocaleString()}
                  </p>
                </div>
                <div>
                  <label className="flex items-center text-xs text-gray-500 uppercase font-semibold mb-2">
                    <Calendar size={14} className="mr-1" /> Last Updated
                  </label>
                  <p className="text-gray-900 text-sm">
                    {new Date(defect.updatedAt).toLocaleString()}
                  </p>
                </div>
              </div>
            </div>
          ) : (
            <div className="space-y-6">
              {/* Upload Section */}
              <div>
                <h3 className="text-lg font-semibold text-gray-800 mb-4">Upload New Image</h3>
                <ImageUpload
                  defectId={defect.defectId}
                  onUploadSuccess={handleUploadSuccess}
                  maxSizeMB={5}
                />
              </div>

              {/* Gallery Section */}
              <div>
                <h3 className="text-lg font-semibold text-gray-800 mb-4">Existing Images</h3>
                <ImageGallery
                  defectId={defect.defectId}
                  refreshTrigger={refreshTrigger}
                />
              </div>
            </div>
          )}
        </div>

        {/* Footer */}
        <div className="flex justify-end gap-3 p-6 border-t border-gray-200 bg-gray-50">
          <button
            onClick={onClose}
            className="px-4 py-2 text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
          >
            Close
          </button>
        </div>
      </div>
    </div>
  );
};

export default DefectDetailModal;
