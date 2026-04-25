import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import authService from "../services/auth.service";

function Register() {
  const [email, setEmail] = useState("");
  const [phone, setPhone] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [success, setSuccess] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    try {
      if (!email && !phone) {
        setError("Please provide either email or phone number.");
        return;
      }
      if (password.length < 6) {
        setError("Password must be at least 6 characters.");
        return;
      }
      const data = {
        email: email || null,
        phone: phone || null,
        password
      };
      await authService.register(data);
      setSuccess(true);
      setTimeout(() => navigate("/login"), 2000);
    } catch (err) {
      setError(err.response?.data?.message || err.response?.data?.email || "Registration failed. Try changing details.");
    }
  };

  return (
    <div className="auth-wrapper glass-panel animate-fade-in" style={{ borderRadius: 0, border: 'none' }}>
      <div className="auth-card glass-panel">
        <h2 className="auth-title">Create Account</h2>
        {error && <div className="error-msg">{error}</div>}
        {success && <div className="error-msg" style={{ background: 'rgba(16, 185, 129, 0.1)', color: '#6ee7b7', borderColor: 'rgba(16, 185, 129, 0.3)' }}>Registration successful! Redirecting...</div>}
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Email (Optional if phone provided)</label>
            <input
              type="email"
              className="form-control"
              placeholder="you@example.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
          </div>
          <div className="form-group">
            <label className="form-label">Phone (Optional if email provided)</label>
            <input
              type="text"
              className="form-control"
              placeholder="+1234567890"
              value={phone}
              onChange={(e) => setPhone(e.target.value)}
            />
          </div>
          <div className="form-group">
            <label className="form-label">Password</label>
            <input
              type="password"
              className="form-control"
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </div>
          <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '1rem' }} disabled={success}>
            Sign Up
          </button>
        </form>
        <div className="auth-switch">
          Already have an account? <Link to="/login">Log in</Link>
        </div>
      </div>
    </div>
  );
}

export default Register;
