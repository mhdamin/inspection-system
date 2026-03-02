import React, { useMemo, useState } from "react";
import { Bell, Car, LogOut, Menu, Search, User, X } from "lucide-react";
import { NavLink, Outlet, useLocation, useNavigate } from "react-router-dom";
import { auth } from "../config";
import { NAV_ITEMS } from "./navigation";

type AppShellProps = {
  onLogout: () => Promise<void>;
};

const AppShell: React.FC<AppShellProps> = ({ onLogout }) => {
  const [mobileNavOpen, setMobileNavOpen] = useState(false);
  const location = useLocation();
  const navigate = useNavigate();
  const username = auth.getUsername() || "User";
  const isAdmin = auth.hasRole("ROLE_ADMIN") || auth.hasRole("ROLE_SUPERADMIN");

  const activeLabel = useMemo(() => {
    const current = NAV_ITEMS.find((item) => item.path === location.pathname);
    return current?.label || "Dashboard";
  }, [location.pathname]);

  const navItems = useMemo(
    () => NAV_ITEMS.filter((item) => !item.requiresAdmin || isAdmin),
    [isAdmin],
  );

  const handleLogout = async () => {
    await onLogout();
    navigate("/");
  };

  return (
    <div className="flex h-screen overflow-hidden bg-slate-100 text-slate-900">
      <aside className="hidden w-72 flex-col border-r border-slate-200 bg-white shadow-panel lg:flex">
        <div className="border-b border-slate-100 px-6 py-5">
          <h1 className="flex items-center gap-2 text-lg font-semibold">
            <Car className="text-brand-600" size={20} />
            FleetGuard Manager
          </h1>
        </div>
        <nav className="flex-1 space-y-1 overflow-auto px-4 py-5">
          {navItems.map((item) => {
            const Icon = item.icon;
            return (
              <NavLink
                key={item.path}
                to={item.path}
                className={({ isActive }) =>
                  `flex items-center gap-3 rounded-xl px-4 py-3 text-sm font-medium transition ${
                    isActive
                      ? "bg-brand-50 text-brand-700"
                      : "text-slate-600 hover:bg-slate-50 hover:text-slate-900"
                  }`
                }
              >
                <Icon size={18} />
                {item.label}
              </NavLink>
            );
          })}
        </nav>
      </aside>

      {mobileNavOpen && (
        <div className="fixed inset-0 z-30 bg-slate-900/30 lg:hidden" onClick={() => setMobileNavOpen(false)}>
          <aside
            className="h-full w-72 bg-white p-4 shadow-xl"
            onClick={(event) => event.stopPropagation()}
          >
            <div className="mb-4 flex items-center justify-between border-b border-slate-200 pb-3">
              <h2 className="flex items-center gap-2 text-base font-semibold">
                <Car className="text-brand-600" size={18} />
                FleetGuard
              </h2>
              <button
                aria-label="Close menu"
                className="rounded-lg p-2 text-slate-500 hover:bg-slate-100"
                onClick={() => setMobileNavOpen(false)}
              >
                <X size={18} />
              </button>
            </div>
            <nav className="space-y-1">
              {navItems.map((item) => {
                const Icon = item.icon;
                return (
                  <NavLink
                    key={item.path}
                    to={item.path}
                    onClick={() => setMobileNavOpen(false)}
                    className={({ isActive }) =>
                      `flex items-center gap-3 rounded-xl px-4 py-3 text-sm font-medium transition ${
                        isActive
                          ? "bg-brand-50 text-brand-700"
                          : "text-slate-600 hover:bg-slate-50 hover:text-slate-900"
                      }`
                    }
                  >
                    <Icon size={18} />
                    {item.label}
                  </NavLink>
                );
              })}
            </nav>
          </aside>
        </div>
      )}

      <main className="flex min-w-0 flex-1 flex-col">
        <header className="flex h-16 items-center justify-between border-b border-slate-200 bg-white px-4 sm:px-6">
          <div className="flex items-center gap-3">
            <button
              aria-label="Open menu"
              className="rounded-lg p-2 text-slate-500 hover:bg-slate-100 lg:hidden"
              onClick={() => setMobileNavOpen(true)}
            >
              <Menu size={20} />
            </button>
            <div>
              <p className="text-xs uppercase tracking-wide text-slate-500">Fleet Operations</p>
              <h2 className="text-sm font-semibold text-slate-900">{activeLabel}</h2>
            </div>
          </div>
          <div className="flex items-center gap-3">
            <div className="relative hidden md:block">
              <Search size={15} className="absolute left-3 top-2.5 text-slate-400" />
              <input
                type="search"
                placeholder="Search records..."
                className="w-60 rounded-lg border border-slate-200 bg-slate-50 py-2 pl-9 pr-3 text-sm focus:border-brand-400 focus:outline-none"
              />
            </div>
            <button className="rounded-lg p-2 text-slate-500 hover:bg-slate-100" aria-label="Notifications">
              <Bell size={18} />
            </button>
            <div className="hidden items-center gap-2 rounded-lg bg-slate-100 px-3 py-2 text-sm font-medium text-slate-700 sm:flex">
              <User size={16} />
              {username}
            </div>
            <button
              onClick={handleLogout}
              className="rounded-lg p-2 text-slate-500 hover:bg-red-50 hover:text-red-600"
              title="Logout"
              aria-label="Logout"
            >
              <LogOut size={18} />
            </button>
          </div>
        </header>
        <div className="min-h-0 flex-1 overflow-auto p-4 sm:p-6 lg:p-8">
          <Outlet />
        </div>
      </main>
    </div>
  );
};

export default AppShell;
