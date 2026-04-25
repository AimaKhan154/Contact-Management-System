import React, { useContext, useState } from "react";
import { AuthContext } from "../context/AuthContext";
import authService from "../services/auth.service";
import { UserCircle, KeyRound, LogOut } from "lucide-react";

function Profile() {
  const { user, logout } = useContext(AuthContext);
  const [showPasswordModal, setShowPasswordModal] = useState(false);
  const [oldPassword, setOldPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [msg, setMsg] = useState({ text: "", type: "" });

  const handlePasswordChange = async (e) => {
    e.preventDefault();
    setMsg({ text: "", type: "" });
    try {
      if (newPassword.length < 6) {
        setMsg({ text: "New password must be at least 6 characters.", type: "error" });
        return;
      }
      await authService.changePassword({ oldPassword, newPassword });
      setMsg({ text: "Password updated successfully!", type: "success" });
      setTimeout(() => {
        setShowPasswordModal(false);
        setOldPassword("");
        setNewPassword("");
        setMsg({ text: "", type: "" });
      }, 2000);
    } catch (err) {
      setMsg({ 
        text: err.response?.data?.message || "Failed to change password.", 
        type: "error" 
      });
    }
  };

  return (
    <div className="animate-fade-in">
      <div className="glass-panel" style={{ padding: '2rem', maxWidth: '600px', margin: '0 auto' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', marginBottom: '2rem' }}>
          <UserCircle size={64} color="var(--primary-color)" />
          <div>
            <h2>User Profile</h2>
            <div style={{ color: 'var(--text-secondary)' }}>Manage your account settings</div>
          </div>
        </div>

        <div className="profile-info">
          {user?.email && (
            <div className="profile-item">
              <span className="profile-item-label">Email Address</span>
              <span className="profile-item-value">{user.email}</span>
            </div>
          )}
          {user?.phone && (
            <div className="profile-item">
              <span className="profile-item-label">Phone Number</span>
              <span className="profile-item-value">{user.phone}</span>
            </div>
          )}
        </div>

        <div style={{ display: 'flex', gap: '1rem', marginTop: '2rem' }}>
          <button className="btn btn-outline" onClick={() => setShowPasswordModal(true)}>
            <KeyRound size={18} /> Change Password
          </button>
          <button className="btn btn-danger" onClick={logout}>
            <LogOut size={18} /> Logout
          </button>
        </div>
      </div>

      {showPasswordModal && (
        <div className="modal-overlay">
          <div className="modal-content glass-panel animate-fade-in">
            <div className="modal-header">
              <h3>Change Password</h3>
              <button className="close-btn" onClick={() => setShowPasswordModal(false)}>×</button>
            </div>
            
            {msg.text && (
              <div className="error-msg" style={msg.type === 'success' ? { background: 'rgba(16, 185, 129, 0.1)', color: '#6ee7b7', borderColor: 'rgba(16, 185, 129, 0.3)' } : {}}>
                {msg.text}
              </div>
            )}

            <form onSubmit={handlePasswordChange}>
              <div className="form-group">
                <label className="form-label">Current Password</label>
                <input 
                  type="password" 
                  className="form-control" 
                  value={oldPassword}
                  onChange={(e) => setOldPassword(e.target.value)}
                  required
                />
              </div>
              <div className="form-group">
                <label className="form-label">New Password</label>
                <input 
                  type="password" 
                  className="form-control" 
                  value={newPassword}
                  onChange={(e) => setNewPassword(e.target.value)}
                  required
                />
              </div>
              <div className="modal-actions">
                <button type="button" className="btn btn-outline" onClick={() => setShowPasswordModal(false)}>Cancel</button>
                <button type="submit" className="btn btn-primary">Reset Password</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default Profile;
