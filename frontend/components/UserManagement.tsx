import React, { useState, useEffect } from 'react';
import { Plus, Edit2, Lock, X, Trash2, Shield } from 'lucide-react';
import { User } from '../types';
import api from '../api';
import { auth } from '../config';

const UserManagement: React.FC = () => {
    const [users, setUsers] = useState<User[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [showAddModal, setShowAddModal] = useState(false);
    const [showEditModal, setShowEditModal] = useState(false);
    const [showAdminResetModal, setShowAdminResetModal] = useState(false);
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [selectedUser, setSelectedUser] = useState<User | null>(null);
    const [assignableRoles, setAssignableRoles] = useState<string[]>([]);

    const currentUsername = auth.getUsername();
    const isSuperAdmin = auth.hasRole('ROLE_SUPERADMIN');
    const isAdmin = auth.hasRole('ROLE_ADMIN');

    const fetchUsers = async () => {
        try {
            setIsLoading(true);
            const data = await api.get('/api/users');
            setUsers(data);
            setError(null);
        } catch (error: any) {
            setError("Failed to fetch users.");
            console.error("Error fetching users:", error);
        } finally {
            setIsLoading(false);
        }
    };

    const fetchAssignableRoles = async () => {
        try {
            const roles = await api.get('/api/users/assignable-roles');
            setAssignableRoles(roles);
        } catch (error) {
            console.error("Error fetching assignable roles:", error);
        }
    };

    useEffect(() => {
        fetchUsers();
        fetchAssignableRoles();
    }, []);

    const canManageUser = (user: User): boolean => {
        // Cannot manage yourself
        if (user.username === currentUsername) {
            return false;
        }

        // Superadmin can manage anyone
        if (isSuperAdmin) {
            return true;
        }

        // Admin can manage users except ADMIN and SUPERADMIN
        if (isAdmin) {
            return !user.roles.includes('ROLE_ADMIN') && !user.roles.includes('ROLE_SUPERADMIN');
        }

        return false;
    };

    const handleAddUser = () => {
        setShowAddModal(true);
    };

    const handleEditUser = (user: User) => {
        setSelectedUser(user);
        setShowEditModal(true);
    };

    const handleAdminResetPassword = (user: User) => {
        setSelectedUser(user);
        setShowAdminResetModal(true);
    };

    const handleDeleteUser = (user: User) => {
        setSelectedUser(user);
        setShowDeleteModal(true);
    };

    const getRoleBadgeColor = (role: string): string => {
        switch (role) {
            case 'ROLE_SUPERADMIN': return 'bg-red-600';
            case 'ROLE_ADMIN': return 'bg-purple-600';
            case 'ROLE_MANAGER': return 'bg-indigo-600';
            case 'ROLE_INSPECTOR': return 'bg-blue-600';
            case 'ROLE_USER': return 'bg-gray-600';
            default: return 'bg-gray-600';
        }
    };

    const getRoleLabel = (role: string): string => {
        return role.replace('ROLE_', '');
    };

    if (isLoading) {
        return (
            <div className="flex items-center justify-center h-64">
                <div className="text-center">
                    <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-gray-900 mx-auto"></div>
                    <p className="mt-4 text-gray-600">Loading users...</p>
                </div>
            </div>
        );
    }

    if (error) {
        return <div className="text-center py-8 text-red-500">Error: {error}</div>;
    }

    const superAdmins = users.filter(u => u.roles.includes('ROLE_SUPERADMIN'));
    const admins = users.filter(u => u.roles.includes('ROLE_ADMIN') && !u.roles.includes('ROLE_SUPERADMIN'));
    const managers = users.filter(u => u.roles.includes('ROLE_MANAGER'));
    const inspectors = users.filter(u => u.roles.includes('ROLE_INSPECTOR'));
    return (
        <div className="space-y-6">
            <div className="flex justify-between items-end">
                <div>
                    <h2 className="text-2xl font-bold text-gray-800">User Management</h2>
                    <p className="text-gray-500">Manage user accounts, roles, and permissions</p>
                </div>
                <button
                    onClick={handleAddUser}
                    className="bg-gray-900 text-white px-4 py-2 rounded-lg flex items-center text-sm font-medium hover:bg-gray-800"
                >
                    <Plus size={16} className="mr-2" /> Add New User
                </button>
            </div>

            {/* Stats Strip */}
            <div className="grid grid-cols-5 gap-4">
                <div className="bg-white p-4 rounded-lg shadow-sm border border-gray-100">
                    <p className="text-xs text-gray-500">Total Users</p>
                    <p className="text-xl font-bold">{users.length}</p>
                </div>
                <div className="bg-white p-4 rounded-lg shadow-sm border border-gray-100">
                    <p className="text-xs text-gray-500">Super Admins</p>
                    <p className="text-xl font-bold text-red-600">{superAdmins.length}</p>
                </div>
                <div className="bg-white p-4 rounded-lg shadow-sm border border-gray-100">
                    <p className="text-xs text-gray-500">Admins</p>
                    <p className="text-xl font-bold text-purple-600">{admins.length}</p>
                </div>
                <div className="bg-white p-4 rounded-lg shadow-sm border border-gray-100">
                    <p className="text-xs text-gray-500">Managers</p>
                    <p className="text-xl font-bold text-indigo-600">{managers.length}</p>
                </div>
                <div className="bg-white p-4 rounded-lg shadow-sm border border-gray-100">
                    <p className="text-xs text-gray-500">Inspectors</p>
                    <p className="text-xl font-bold text-blue-600">{inspectors.length}</p>
                </div>
            </div>

            <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
                <table className="w-full text-left border-collapse">
                    <thead>
                        <tr className="bg-gray-50 border-b border-gray-100 text-xs uppercase text-gray-500 font-semibold">
                            <th className="px-6 py-4">ID</th>
                            <th className="px-6 py-4">Username</th>
                            <th className="px-6 py-4">Roles</th>
                            <th className="px-6 py-4">Status</th>
                            <th className="px-6 py-4 text-right">Actions</th>
                        </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-100">
                        {users.map(user => {
                            const canManage = canManageUser(user);
                            const isCurrentUser = user.username === currentUsername;

                            return (
                                <tr key={user.id} className="hover:bg-gray-50 text-sm">
                                    <td className="px-6 py-4 font-medium text-gray-800">{user.id}</td>
                                    <td className="px-6 py-4">
                                        <div className="flex items-center gap-3">
                                            <div className={`w-8 h-8 rounded-full flex items-center justify-center text-white font-bold text-xs ${
                                                user.roles.includes('ROLE_SUPERADMIN') ? 'bg-red-600' :
                                                user.roles.includes('ROLE_ADMIN') ? 'bg-purple-600' :
                                                user.roles.includes('ROLE_MANAGER') ? 'bg-indigo-600' :
                                                user.roles.includes('ROLE_INSPECTOR') ? 'bg-blue-600' :
                                                'bg-gray-600'
                                            }`}>
                                                {user.username.substring(0, 2).toUpperCase()}
                                            </div>
                                            <div>
                                                <span className="font-medium text-gray-900">{user.username}</span>
                                                {isCurrentUser && (
                                                    <span className="ml-2 text-xs bg-blue-100 text-blue-700 px-2 py-0.5 rounded-full">
                                                        You
                                                    </span>
                                                )}
                                            </div>
                                        </div>
                                    </td>
                                    <td className="px-6 py-4">
                                        <div className="flex flex-wrap gap-1">
                                            {user.roles.map(role => (
                                                <span
                                                    key={role}
                                                    className={`px-2 py-1 rounded-full text-xs font-bold text-white ${getRoleBadgeColor(role)}`}
                                                >
                                                    {getRoleLabel(role)}
                                                </span>
                                            ))}
                                        </div>
                                    </td>
                                    <td className="px-6 py-4">
                                        <span className="text-green-600 bg-green-50 px-2 py-1 rounded-full text-xs font-medium border border-green-200">
                                            Active
                                        </span>
                                    </td>
                                    <td className="px-6 py-4 text-right">
                                        <div className="flex justify-end gap-2">
                                            {canManage && (
                                                <>
                                                    <button
                                                        onClick={() => handleEditUser(user)}
                                                        className="text-gray-400 hover:text-blue-600"
                                                        title="Edit User"
                                                    >
                                                        <Edit2 size={16} />
                                                    </button>
                                                    <button
                                                        onClick={() => handleAdminResetPassword(user)}
                                                        className="text-gray-400 hover:text-amber-600"
                                                        title="Reset Password"
                                                    >
                                                        <Lock size={16} />
                                                    </button>
                                                    <button
                                                        onClick={() => handleDeleteUser(user)}
                                                        className="text-gray-400 hover:text-red-600"
                                                        title="Delete User"
                                                    >
                                                        <Trash2 size={16} />
                                                    </button>
                                                </>
                                            )}
                                            {!canManage && !isCurrentUser && (
                                                <span className="text-xs text-gray-400 flex items-center gap-1">
                                                    <Shield size={12} /> Protected
                                                </span>
                                            )}
                                        </div>
                                    </td>
                                </tr>
                            );
                        })}
                    </tbody>
                </table>
            </div>

            {/* Add User Modal */}
            {showAddModal && (
                <AddUserModal
                    assignableRoles={assignableRoles}
                    onClose={() => setShowAddModal(false)}
                    onSuccess={() => {
                        setShowAddModal(false);
                        fetchUsers();
                    }}
                />
            )}

            {/* Edit User Modal */}
            {showEditModal && selectedUser && (
                <EditUserModal
                    user={selectedUser}
                    assignableRoles={assignableRoles}
                    onClose={() => {
                        setShowEditModal(false);
                        setSelectedUser(null);
                    }}
                    onSuccess={() => {
                        setShowEditModal(false);
                        setSelectedUser(null);
                        fetchUsers();
                    }}
                />
            )}

            {/* Admin Reset Password Modal */}
            {showAdminResetModal && selectedUser && (
                <AdminResetPasswordModal
                    user={selectedUser}
                    onClose={() => {
                        setShowAdminResetModal(false);
                        setSelectedUser(null);
                    }}
                    onSuccess={() => {
                        setShowAdminResetModal(false);
                        setSelectedUser(null);
                    }}
                />
            )}

            {/* Delete User Modal */}
            {showDeleteModal && selectedUser && (
                <DeleteUserModal
                    user={selectedUser}
                    onClose={() => {
                        setShowDeleteModal(false);
                        setSelectedUser(null);
                    }}
                    onSuccess={() => {
                        setShowDeleteModal(false);
                        setSelectedUser(null);
                        fetchUsers();
                    }}
                />
            )}
        </div>
    );
};

// Add User Modal Component
const AddUserModal: React.FC<{
    assignableRoles: string[];
    onClose: () => void;
    onSuccess: () => void;
}> = ({ assignableRoles, onClose, onSuccess }) => {
    const [formData, setFormData] = useState({
        username: '',
        password: '',
        confirmPassword: '',
        roles: ['ROLE_USER']
    });
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError(null);

        if (formData.password !== formData.confirmPassword) {
            setError('Passwords do not match');
            return;
        }

        if (formData.password.length < 4) {
            setError('Password must be at least 4 characters');
            return;
        }

        if (formData.roles.length === 0) {
            setError('Please select at least one role');
            return;
        }

        setIsSubmitting(true);

        try {
            await api.post('/api/auth/register', {
                username: formData.username,
                password: formData.password,
                roles: formData.roles
            });
            onSuccess();
        } catch (err: any) {
            setError(err.message || 'Failed to create user');
            console.error('Error creating user:', err);
        } finally {
            setIsSubmitting(false);
        }
    };

    const toggleRole = (role: string) => {
        setFormData(prev => ({
            ...prev,
            roles: prev.roles.includes(role)
                ? prev.roles.filter(r => r !== role)
                : [...prev.roles, role]
        }));
    };

    const getRoleLabel = (role: string): string => {
        return role.replace('ROLE_', '');
    };

    return (
        <div className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4">
            <div className="bg-white rounded-xl shadow-2xl max-w-md w-full overflow-hidden">
                <div className="flex justify-between items-center p-6 border-b">
                    <h3 className="text-xl font-bold text-gray-800">Add New User</h3>
                    <button onClick={onClose} className="text-gray-400 hover:text-gray-700">
                        <X size={24} />
                    </button>
                </div>

                <form onSubmit={handleSubmit} className="p-6 space-y-4">
                    {error && (
                        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg text-sm">
                            {error}
                        </div>
                    )}

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Username *
                        </label>
                        <input
                            type="text"
                            value={formData.username}
                            onChange={(e) => setFormData({ ...formData, username: e.target.value })}
                            required
                            className="w-full p-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:outline-none"
                            placeholder="Enter username"
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Password *
                        </label>
                        <input
                            type="password"
                            value={formData.password}
                            onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                            required
                            minLength={4}
                            className="w-full p-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:outline-none"
                            placeholder="Enter password"
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Confirm Password *
                        </label>
                        <input
                            type="password"
                            value={formData.confirmPassword}
                            onChange={(e) => setFormData({ ...formData, confirmPassword: e.target.value })}
                            required
                            className="w-full p-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:outline-none"
                            placeholder="Confirm password"
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            Roles * (You can assign: {assignableRoles.map(r => getRoleLabel(r)).join(', ')})
                        </label>
                        <div className="space-y-2">
                            {assignableRoles.map(role => (
                                <label key={role} className="flex items-center">
                                    <input
                                        type="checkbox"
                                        checked={formData.roles.includes(role)}
                                        onChange={() => toggleRole(role)}
                                        className="mr-2 rounded"
                                    />
                                    <span className="text-sm">{getRoleLabel(role)}</span>
                                </label>
                            ))}
                        </div>
                    </div>

                    <div className="flex justify-end gap-3 pt-4">
                        <button
                            type="button"
                            onClick={onClose}
                            className="px-4 py-2 text-sm font-medium text-gray-600 hover:bg-gray-100 rounded-lg"
                            disabled={isSubmitting}
                        >
                            Cancel
                        </button>
                        <button
                            type="submit"
                            className="px-6 py-2 text-sm font-medium text-white bg-gray-900 hover:bg-gray-800 rounded-lg disabled:opacity-50"
                            disabled={isSubmitting}
                        >
                            {isSubmitting ? 'Creating...' : 'Create User'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

// Edit User Modal Component
const EditUserModal: React.FC<{
    user: User;
    assignableRoles: string[];
    onClose: () => void;
    onSuccess: () => void;
}> = ({ user, assignableRoles, onClose, onSuccess }) => {
    const [formData, setFormData] = useState({
        username: user.username,
        roles: user.roles
    });
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError(null);

        if (formData.roles.length === 0) {
            setError('Please select at least one role');
            return;
        }

        setIsSubmitting(true);

        try {
            await api.put(`/api/users/${user.id}`, {
                username: formData.username,
                roles: formData.roles
            });
            onSuccess();
        } catch (err: any) {
            setError(err.message || 'Failed to update user');
            console.error('Error updating user:', err);
        } finally {
            setIsSubmitting(false);
        }
    };

    const toggleRole = (role: string) => {
        setFormData(prev => ({
            ...prev,
            roles: prev.roles.includes(role)
                ? prev.roles.filter(r => r !== role)
                : [...prev.roles, role]
        }));
    };

    const getRoleLabel = (role: string): string => {
        return role.replace('ROLE_', '');
    };

    return (
        <div className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4">
            <div className="bg-white rounded-xl shadow-2xl max-w-md w-full overflow-hidden">
                <div className="flex justify-between items-center p-6 border-b">
                    <h3 className="text-xl font-bold text-gray-800">Edit User: {user.username}</h3>
                    <button onClick={onClose} className="text-gray-400 hover:text-gray-700">
                        <X size={24} />
                    </button>
                </div>

                <form onSubmit={handleSubmit} className="p-6 space-y-4">
                    {error && (
                        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg text-sm">
                            {error}
                        </div>
                    )}

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Username *
                        </label>
                        <input
                            type="text"
                            value={formData.username}
                            onChange={(e) => setFormData({ ...formData, username: e.target.value })}
                            required
                            className="w-full p-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:outline-none"
                            placeholder="Enter username"
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            Roles * (You can assign: {assignableRoles.map(r => getRoleLabel(r)).join(', ')})
                        </label>
                        <div className="space-y-2">
                            {assignableRoles.map(role => (
                                <label key={role} className="flex items-center">
                                    <input
                                        type="checkbox"
                                        checked={formData.roles.includes(role)}
                                        onChange={() => toggleRole(role)}
                                        className="mr-2 rounded"
                                    />
                                    <span className="text-sm">{getRoleLabel(role)}</span>
                                </label>
                            ))}
                        </div>
                    </div>

                    <div className="flex justify-end gap-3 pt-4">
                        <button
                            type="button"
                            onClick={onClose}
                            className="px-4 py-2 text-sm font-medium text-gray-600 hover:bg-gray-100 rounded-lg"
                            disabled={isSubmitting}
                        >
                            Cancel
                        </button>
                        <button
                            type="submit"
                            className="px-6 py-2 text-sm font-medium text-white bg-gray-900 hover:bg-gray-800 rounded-lg disabled:opacity-50"
                            disabled={isSubmitting}
                        >
                            {isSubmitting ? 'Updating...' : 'Update User'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

// Admin Reset Password Modal Component
const AdminResetPasswordModal: React.FC<{
    user: User;
    onClose: () => void;
    onSuccess: () => void;
}> = ({ user, onClose, onSuccess }) => {
    const [formData, setFormData] = useState({
        newPassword: '',
        confirmPassword: ''
    });
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError(null);

        if (formData.newPassword !== formData.confirmPassword) {
            setError('Passwords do not match');
            return;
        }

        if (formData.newPassword.length < 4) {
            setError('Password must be at least 4 characters');
            return;
        }

        setIsSubmitting(true);

        try {
            await api.post(`/api/users/${user.id}/reset-password`, formData);
            onSuccess();
        } catch (err: any) {
            setError(err.message || 'Failed to reset password');
            console.error('Error resetting password:', err);
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <div className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4">
            <div className="bg-white rounded-xl shadow-2xl max-w-md w-full overflow-hidden">
                <div className="flex justify-between items-center p-6 border-b">
                    <div>
                        <h3 className="text-xl font-bold text-gray-800">Admin Reset Password</h3>
                        <p className="text-sm text-gray-500 mt-1">Resetting password for: {user.username}</p>
                    </div>
                    <button onClick={onClose} className="text-gray-400 hover:text-gray-700">
                        <X size={24} />
                    </button>
                </div>

                <form onSubmit={handleSubmit} className="p-6 space-y-4">
                    <div className="bg-amber-50 border border-amber-200 text-amber-800 px-4 py-3 rounded-lg text-sm">
                        <p className="font-medium">Admin Password Reset</p>
                        <p className="text-xs mt-1">No current password required. The user will be able to login with the new password immediately.</p>
                    </div>

                    {error && (
                        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg text-sm">
                            {error}
                        </div>
                    )}

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            New Password *
                        </label>
                        <input
                            type="password"
                            value={formData.newPassword}
                            onChange={(e) => setFormData({ ...formData, newPassword: e.target.value })}
                            required
                            minLength={4}
                            className="w-full p-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:outline-none"
                            placeholder="Enter new password"
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Confirm New Password *
                        </label>
                        <input
                            type="password"
                            value={formData.confirmPassword}
                            onChange={(e) => setFormData({ ...formData, confirmPassword: e.target.value })}
                            required
                            className="w-full p-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:outline-none"
                            placeholder="Confirm new password"
                        />
                    </div>

                    <div className="flex justify-end gap-3 pt-4">
                        <button
                            type="button"
                            onClick={onClose}
                            className="px-4 py-2 text-sm font-medium text-gray-600 hover:bg-gray-100 rounded-lg"
                            disabled={isSubmitting}
                        >
                            Cancel
                        </button>
                        <button
                            type="submit"
                            className="px-6 py-2 text-sm font-medium text-white bg-amber-600 hover:bg-amber-700 rounded-lg disabled:opacity-50"
                            disabled={isSubmitting}
                        >
                            {isSubmitting ? 'Resetting...' : 'Reset Password'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

// Delete User Modal Component
const DeleteUserModal: React.FC<{
    user: User;
    onClose: () => void;
    onSuccess: () => void;
}> = ({ user, onClose, onSuccess }) => {
    const [isDeleting, setIsDeleting] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [confirmText, setConfirmText] = useState('');

    const handleDelete = async () => {
        setError(null);

        if (confirmText !== user.username) {
            setError('Username does not match');
            return;
        }

        setIsDeleting(true);

        try {
            await api.delete(`/api/users/${user.id}`);
            onSuccess();
        } catch (err: any) {
            setError(err.message || 'Failed to delete user');
            console.error('Error deleting user:', err);
        } finally {
            setIsDeleting(false);
        }
    };

    return (
        <div className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4">
            <div className="bg-white rounded-xl shadow-2xl max-w-md w-full overflow-hidden">
                <div className="flex justify-between items-center p-6 border-b border-red-100 bg-red-50">
                    <div className="flex items-center gap-3">
                        <div className="w-10 h-10 rounded-full bg-red-100 flex items-center justify-center">
                            <Trash2 size={20} className="text-red-600" />
                        </div>
                        <div>
                            <h3 className="text-xl font-bold text-gray-800">Delete User</h3>
                            <p className="text-sm text-red-600">This action cannot be undone</p>
                        </div>
                    </div>
                    <button onClick={onClose} className="text-gray-400 hover:text-gray-700">
                        <X size={24} />
                    </button>
                </div>

                <div className="p-6 space-y-4">
                    <div className="bg-red-50 border border-red-200 text-red-800 px-4 py-3 rounded-lg text-sm">
                        <p className="font-medium">Warning: Permanent Deletion</p>
                        <p className="text-xs mt-1">
                            You are about to delete user <strong>{user.username}</strong>.
                            This will permanently remove the user and all associated data.
                        </p>
                    </div>

                    {error && (
                        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg text-sm">
                            {error}
                        </div>
                    )}

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Type <strong>{user.username}</strong> to confirm deletion
                        </label>
                        <input
                            type="text"
                            value={confirmText}
                            onChange={(e) => setConfirmText(e.target.value)}
                            className="w-full p-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-500 focus:outline-none"
                            placeholder={`Type "${user.username}" to confirm`}
                        />
                    </div>

                    <div className="flex justify-end gap-3 pt-4">
                        <button
                            type="button"
                            onClick={onClose}
                            className="px-4 py-2 text-sm font-medium text-gray-600 hover:bg-gray-100 rounded-lg"
                            disabled={isDeleting}
                        >
                            Cancel
                        </button>
                        <button
                            onClick={handleDelete}
                            className="px-6 py-2 text-sm font-medium text-white bg-red-600 hover:bg-red-700 rounded-lg disabled:opacity-50"
                            disabled={isDeleting || confirmText !== user.username}
                        >
                            {isDeleting ? 'Deleting...' : 'Delete User'}
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default UserManagement;
