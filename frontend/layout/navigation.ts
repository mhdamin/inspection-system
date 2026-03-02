import {
  AlertTriangle,
  ClipboardList,
  FileText,
  History,
  LayoutDashboard,
  Settings,
  Users,
  type LucideIcon,
  Car,
} from "lucide-react";

export type NavItemConfig = {
  label: string;
  path: string;
  icon: LucideIcon;
  requiresAdmin?: boolean;
};

export const NAV_ITEMS: NavItemConfig[] = [
  { label: "Dashboard", path: "/dashboard", icon: LayoutDashboard },
  { label: "Vehicle Management", path: "/vehicles", icon: Car },
  { label: "Checklist Management", path: "/checklist", icon: ClipboardList },
  { label: "Inspection History", path: "/history", icon: History },
  { label: "Defect Management", path: "/defects", icon: AlertTriangle },
  { label: "Audit Trail", path: "/audit", icon: FileText },
  { label: "Reports", path: "/reports", icon: Settings },
  { label: "User Management", path: "/users", icon: Users, requiresAdmin: true },
];
