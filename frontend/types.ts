export type ViewState = 'dashboard' | 'vehicles' | 'checklist' | 'history' | 'defects' | 'audit' | 'reports' | 'users';

// Vehicle type matching backend VehicleResponseDTO
export interface Vehicle {
  id: string;
  plateNumber: string;
  model: string;
  manufacturer: string;
  year: number;
  status: string;
  createdAt?: string;
  updatedAt?: string;
}

// Vehicle statistics matching backend VehicleStatsDTO
export interface VehicleStats {
  totalVehicles: number;
  availableVehicles: number;
  rentedVehicles: number;
  maintenanceVehicles: number;
}

// Activity log matching backend VehicleChangeLogResponseDTO
export interface Activity {
  id: string;
  checklistId: string;
  changeType: string;
  oldVehiclePlate: string | null;
  newVehiclePlate: string | null;
  reason: string;
  timestamp: string;
  staffName: string;
}

export interface User {
  id: number;
  username: string;
  roles: string[];
}

export interface LogEntry {
  id: string;
  timestamp: string;
  user: string;
  action: string;
  resource: string;
  details: string;
  ip: string;
  status: 'Success' | 'Warning' | 'Critical';
}

// Checklist Data Structure
export interface InspectionPoint {
  id: number;
  x: number;
  y: number; // Percentage positions
  label: string;
  position?: string; // Position description (e.g., "Hood", "Left Front Door")
  status?: 'Normal' | 'Abnormal' | 'N/A' | 'Not Inspected';
  notes?: string;
}

export interface ChecklistData {
  vehicleId: string;
  plate: string;
  makeModel: string;
  odometer: string;
  fuelLevel: string;
  type: string;
  exteriorPoints: InspectionPoint[];
  interior: {
    dashboard: string;
    seats: string;
    carpets: string;
    windows: string;
    electronics: string;
    safety: string;
  };
  signature: {
    customerName: string;
    inspectorName: string;
    date: string;
    signed: boolean;
  };
}

// Backend DTO types for Checklist
export interface VehicleSummary {
  id: string;
  plateNumber: string;
  model: string;
  manufacturer: string;
  year: number;
}

export interface ChecklistResponse {
  id: string;
  checklistNumber: string;
  rentalStartDate: string;
  rentalEndDate?: string;
  customerName: string;
  customerPhone: string;
  staffName: string;
  rentalType: string;
  vehicle: VehicleSummary;
  createdAt: string;
}

export interface SubChecklistResponse {
  id: string;
  type: string;
  remarks?: string;
  createdAt: string;
  // Add inspection items when needed
}

export interface DefectResponse {
  defectId: number;
  checklistId: string;
  itemId?: number;
  defectType: 'SCRATCH' | 'DENT' | 'CRACK';
  description: string;
  diagramX?: number;
  diagramY?: number;
  status?: 'NEW' | 'IN_PROGRESS' | 'RESOLVED';
  createdAt: string;
  updatedAt: string;
  images?: DefectImageResponse[];
}

export interface DefectImageResponse {
  imageId: number;
  defectId: number;
  imageUrl: string;
  fileName: string;
  fileSize: number;
  uploadedAt: string;
  uploadedBy?: number;
}

// Extended defect response with checklist and vehicle info
export interface DefectWithDetails extends DefectResponse {
  checklistNumber?: string;
  vehiclePlate?: string;
  vehicleInfo?: string;
  customerName?: string;
}

// Dashboard types
export interface DashboardStats extends VehicleStats {
  todayInspections: number;
  pendingApprovals: number;
  activeDefects: number;
  overdueInspections: number;
  inspectionsTrend: number; // percentage change vs last week
  defectsTrend: number;
  utilizationRate: number; // percentage
}

export interface DashboardMetricsResponse {
  todayInspections?: number;
  pendingApprovals?: number;
  activeDefects?: number;
  overdueInspections?: number;
  inspectionsTrend?: number;
  defectsTrend?: number;
}

export interface Alert {
  id: string;
  type: 'critical' | 'warning' | 'info';
  title: string;
  message: string;
  timestamp: string;
  actionLabel?: string;
  actionUrl?: string;
}

export interface InspectionSummary {
  total: number;
  preRental: number;
  postRental: number;
  periodic: number;
  avgCompletionTime: number; // in minutes
  passRate: number; // percentage
}

export interface QuickAction {
  id: string;
  label: string;
  icon: string;
  onClick: () => void;
  roles: string[]; // which roles can see this action
}
