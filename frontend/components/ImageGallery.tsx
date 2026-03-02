import React, { useState, useEffect } from 'react';
import { X, Trash2, Download, ZoomIn, Loader, AlertCircle, Image as ImageIcon } from 'lucide-react';
import api from '../api';
import { DefectImageResponse } from '../types';

interface ImageGalleryProps {
  defectId: number;
  onImageDeleted?: (imageId: number) => void;
  refreshTrigger?: number; // External trigger to refresh the gallery
  showUploadButton?: boolean;
}

const ImageGallery: React.FC<ImageGalleryProps> = ({
  defectId,
  onImageDeleted,
  refreshTrigger = 0,
  showUploadButton = false
}) => {
  const [images, setImages] = useState<DefectImageResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedImage, setSelectedImage] = useState<DefectImageResponse | null>(null);
  const [deletingImageId, setDeletingImageId] = useState<number | null>(null);
  const [deleteError, setDeleteError] = useState<string | null>(null);

  useEffect(() => {
    fetchImages();
  }, [defectId, refreshTrigger]);

  const fetchImages = async () => {
    setIsLoading(true);
    setError(null);
    try {
      const data = await api.get<DefectImageResponse[]>(`/api/defects/${defectId}/images`);
      setImages(data);
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to fetch images';
      setError(errorMessage);
      console.error('Error fetching images:', err);
    } finally {
      setIsLoading(false);
    }
  };

  const handleDelete = async (imageId: number) => {
    if (!confirm('Are you sure you want to delete this image? This action cannot be undone.')) {
      return;
    }

    setDeletingImageId(imageId);
    setDeleteError(null);
    try {
      await api.delete(`/api/defects/images/${imageId}`);

      // Remove image from local state
      setImages(prev => prev.filter(img => img.imageId !== imageId));

      if (onImageDeleted) {
        onImageDeleted(imageId);
      }

      // Close lightbox if deleted image was selected
      if (selectedImage?.imageId === imageId) {
        setSelectedImage(null);
      }
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to delete image';
      setDeleteError(`Delete failed: ${errorMessage}`);
      console.error('Error deleting image:', err);
    } finally {
      setDeletingImageId(null);
    }
  };

  const handleDownload = (image: DefectImageResponse) => {
    const link = document.createElement('a');
    link.href = image.imageUrl;
    link.download = image.fileName;
    link.target = '_blank';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  const formatFileSize = (bytes: number): string => {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round((bytes / Math.pow(k, i)) * 100) / 100 + ' ' + sizes[i];
  };

  const formatDate = (dateString: string): string => {
    const date = new Date(dateString);
    return date.toLocaleDateString() + ' ' + date.toLocaleTimeString();
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center py-12">
        <div className="text-center">
          <Loader className="animate-spin mx-auto mb-4 text-gray-400" size={40} />
          <p className="text-gray-500">Loading images...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-red-50 border border-red-200 rounded-lg p-6 text-center">
        <AlertCircle className="mx-auto mb-3 text-red-500" size={48} />
        <h3 className="text-lg font-semibold text-red-700 mb-2">Error Loading Images</h3>
        <p className="text-red-600 mb-4">{error}</p>
        <button
          onClick={fetchImages}
          className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors"
        >
          Retry
        </button>
      </div>
    );
  }

  if (images.length === 0) {
    return (
      <div className="bg-gray-50 border border-gray-200 rounded-lg p-8 text-center">
        <ImageIcon className="mx-auto mb-3 text-gray-400" size={48} />
        <h3 className="text-lg font-semibold text-gray-700 mb-1">No Images Uploaded</h3>
        <p className="text-gray-600">
          {showUploadButton
            ? 'Upload images to document this defect'
            : 'No images have been uploaded for this defect yet'}
        </p>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      {deleteError && (
        <div className="rounded-lg border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700">{deleteError}</div>
      )}
      {/* Image Grid */}
      <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
        {images.map((image) => (
          <div
            key={image.imageId}
            className="relative group bg-white border border-gray-200 rounded-lg overflow-hidden hover:shadow-md transition-shadow"
          >
            {/* Image Thumbnail */}
            <div className="aspect-square bg-gray-100 relative">
              <img
                src={image.imageUrl}
                alt={image.fileName}
                className="w-full h-full object-cover cursor-pointer"
                onClick={() => setSelectedImage(image)}
              />

              {/* Overlay on Hover */}
              <div className="absolute inset-0 bg-black bg-opacity-0 group-hover:bg-opacity-40 transition-all duration-200 flex items-center justify-center">
                <button
                  onClick={() => setSelectedImage(image)}
                  className="opacity-0 group-hover:opacity-100 transition-opacity p-2 bg-white rounded-full hover:bg-gray-100"
                  title="View full size"
                >
                  <ZoomIn size={20} className="text-gray-700" />
                </button>
              </div>
            </div>

            {/* Image Info */}
            <div className="p-3 space-y-1">
              <p className="text-xs font-medium text-gray-800 truncate" title={image.fileName}>
                {image.fileName}
              </p>
              <p className="text-xs text-gray-500">{formatFileSize(image.fileSize)}</p>
              <p className="text-xs text-gray-400">{formatDate(image.uploadedAt)}</p>
            </div>

            {/* Action Buttons */}
            <div className="absolute top-2 right-2 flex gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
              <button
                onClick={() => handleDownload(image)}
                className="p-1.5 bg-white rounded-full shadow-md hover:bg-gray-100 transition-colors"
                title="Download"
              >
                <Download size={14} className="text-gray-700" />
              </button>
              <button
                onClick={() => handleDelete(image.imageId)}
                disabled={deletingImageId === image.imageId}
                className="p-1.5 bg-white rounded-full shadow-md hover:bg-red-100 transition-colors disabled:opacity-50"
                title="Delete"
              >
                {deletingImageId === image.imageId ? (
                  <Loader size={14} className="animate-spin text-gray-700" />
                ) : (
                  <Trash2 size={14} className="text-red-600" />
                )}
              </button>
            </div>
          </div>
        ))}
      </div>

      {/* Lightbox Modal */}
      {selectedImage && (
        <div
          className="fixed inset-0 z-50 bg-black bg-opacity-90 flex items-center justify-center p-4"
          onClick={() => setSelectedImage(null)}
        >
          <div className="relative max-w-6xl max-h-full" onClick={(e) => e.stopPropagation()}>
            {/* Close Button */}
            <button
              onClick={() => setSelectedImage(null)}
              className="absolute -top-12 right-0 p-2 text-white hover:text-gray-300 transition-colors"
            >
              <X size={32} />
            </button>

            {/* Image */}
            <img
              src={selectedImage.imageUrl}
              alt={selectedImage.fileName}
              className="max-w-full max-h-[80vh] object-contain rounded-lg"
            />

            {/* Image Details */}
            <div className="absolute -bottom-20 left-0 right-0 bg-black bg-opacity-75 text-white p-4 rounded-b-lg">
              <p className="font-medium mb-1">{selectedImage.fileName}</p>
              <div className="flex gap-4 text-sm text-gray-300">
                <span>{formatFileSize(selectedImage.fileSize)}</span>
                <span>•</span>
                <span>Uploaded: {formatDate(selectedImage.uploadedAt)}</span>
              </div>
            </div>

            {/* Action Buttons in Lightbox */}
            <div className="absolute top-0 left-0 flex gap-2 p-4">
              <button
                onClick={() => handleDownload(selectedImage)}
                className="px-4 py-2 bg-white text-gray-800 rounded-lg hover:bg-gray-100 transition-colors flex items-center gap-2"
              >
                <Download size={16} />
                Download
              </button>
              <button
                onClick={() => handleDelete(selectedImage.imageId)}
                disabled={deletingImageId === selectedImage.imageId}
                className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors flex items-center gap-2 disabled:opacity-50"
              >
                {deletingImageId === selectedImage.imageId ? (
                  <>
                    <Loader size={16} className="animate-spin" />
                    Deleting...
                  </>
                ) : (
                  <>
                    <Trash2 size={16} />
                    Delete
                  </>
                )}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default ImageGallery;
