import React, { useContext } from "react";
import { Link, useLocation } from "react-router-dom";
import { AuthContext } from "../context/AuthContext";
import { LayoutDashboard, UserCircle, LogOut } from "lucide-react";

function Navbar() {
  const { user, logout } = useContext(AuthContext);
  const location = useLocation();

  if (!user) return null;

  return (
    <nav className="navbar glass-panel">
      <div className="nav-brand-wrap">
        <div className="nav-brand">ContactSphere</div>
        <div className="nav-tagline">Smart Contact Management System</div>
      </div>
      <div className="nav-links">
        <Link 
          to="/dashboard" 
          className={`nav-link ${location.pathname === "/dashboard" ? "active" : ""}`}
        >
          <LayoutDashboard size={18} className="nav-link-icon" />
          Dashboard
        </Link>
        <Link 
          to="/profile" 
          className={`nav-link ${location.pathname === "/profile" ? "active" : ""}`}
        >
          <UserCircle size={18} className="nav-link-icon" />
          Profile
        </Link>
        <button onClick={logout} className="icon-btn" title="Logout">
          <LogOut size={20} />
        </button>
      </div>
    </nav>
  );
}

export default Navbar;
