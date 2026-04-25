import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import contactService from "../services/contact.service";

function ContactDetails() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [contact, setContact] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchContact = async () => {
      try {
        const res = await contactService.getContact(id);
        setContact(res.data);
      } catch (err) {
        alert(err.response?.data?.message || "Failed to load contact details.");
      } finally {
        setLoading(false);
      }
    };

    fetchContact();
  }, [id]);

  if (loading) {
    return <div className="glass-panel" style={{ padding: "1.5rem" }}>Loading contact details...</div>;
  }

  if (!contact) {
    return (
      <div className="glass-panel" style={{ padding: "1.5rem" }}>
        <p>Contact details are not available.</p>
        <button className="btn btn-outline" onClick={() => navigate("/dashboard")}>Back to Dashboard</button>
      </div>
    );
  }

  return (
    <div className="animate-fade-in">
      <div className="glass-panel" style={{ padding: "1.5rem", maxWidth: "800px", margin: "0 auto" }}>
        <h2 style={{ marginBottom: "1rem" }}>Contact Profile</h2>
        <p><strong>Name:</strong> {contact.title} {contact.firstName} {contact.lastName}</p>

        <h3 style={{ marginTop: "1.5rem" }}>Emails</h3>
        {contact.emails?.length ? (
          contact.emails.map((email) => (
            <p key={email.id || `${email.emailAddress}-${email.label}`}>
              {email.emailAddress} ({email.label})
            </p>
          ))
        ) : (
          <p>No email addresses.</p>
        )}

        <h3 style={{ marginTop: "1.5rem" }}>Phone Numbers</h3>
        {contact.phones?.length ? (
          contact.phones.map((phone) => (
            <p key={phone.id || `${phone.phoneNumber}-${phone.label}`}>
              {phone.phoneNumber} ({phone.label})
            </p>
          ))
        ) : (
          <p>No phone numbers.</p>
        )}

        <div style={{ marginTop: "1.5rem" }}>
          <button
            className="btn btn-primary"
            style={{ marginRight: "0.75rem" }}
            onClick={() => navigate("/dashboard", { state: { editContactId: contact.id } })}
          >
            Edit / Update Contact
          </button>
          <button className="btn btn-outline" onClick={() => navigate("/dashboard")}>Back to Dashboard</button>
        </div>
      </div>
    </div>
  );
}

export default ContactDetails;
