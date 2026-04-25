import React, { useState, useEffect, useRef } from "react";
import contactService from "../services/contact.service";
import { Search, Plus, Edit2, Trash2, ChevronLeft, ChevronRight, X, Mail, Phone, Download, Upload, Eye } from "lucide-react";
import { useLocation, useNavigate } from "react-router-dom";

function Dashboard() {
  const navigate = useNavigate();
  const location = useLocation();
  const [contacts, setContacts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [search, setSearch] = useState("");
  const [modalType, setModalType] = useState(null);
  const [selectedContact, setSelectedContact] = useState(null);
  const fileInputRef = useRef(null);
  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    title: "Mr.",
    emails: [{ emailAddress: "", label: "Personal" }],
    phones: [{ phoneNumber: "", label: "Personal" }]
  });

  const fetchContacts = async () => {
    setLoading(true);
    try {
      const res = await contactService.getAllContacts(page, 10, search);
      setContacts(res.data.content);
      setTotalPages(res.data.totalPages);
    } catch (err) {
      console.error(err);
      if (err.response?.status !== 401) {
        alert(err.response?.data?.message || "Unable to load contacts.");
      }
      setContacts([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchContacts();
  }, [page, search]);

  useEffect(() => {
    const editContactId = location.state?.editContactId;
    if (!editContactId || contacts.length === 0) return;
    const targetContact = contacts.find((item) => item.id === editContactId);
    if (targetContact) {
      openUpdateModal(targetContact);
      navigate(location.pathname, { replace: true, state: {} });
    }
  }, [contacts, location.state, location.pathname, navigate]);

  const handleSearch = (e) => {
    setSearch(e.target.value);
    setPage(0);
  };

  const openCreateModal = () => {
    setFormData({
      firstName: "",
      lastName: "",
      title: "Mr.",
      emails: [{ emailAddress: "", label: "Personal" }],
      phones: [{ phoneNumber: "", label: "Personal" }]
    });
    setModalType("create");
  };

  const toEditableArray = (items, valueKey, defaultLabel) => {
    if (!items || items.length === 0) {
      return [{ [valueKey]: "", label: defaultLabel }];
    }
    return items.map((item) => ({
      [valueKey]: item?.[valueKey] || "",
      label: item?.label || defaultLabel,
    }));
  };

  const openUpdateModal = async (contact) => {
    try {
      const response = await contactService.getContact(contact.id);
      const fullContact = response.data;
      setSelectedContact(fullContact);
      setFormData({
        firstName: fullContact.firstName || "",
        lastName: fullContact.lastName || "",
        title: fullContact.title || "Mr.",
        emails: toEditableArray(fullContact.emails, "emailAddress", "Personal"),
        phones: toEditableArray(fullContact.phones, "phoneNumber", "Personal")
      });
      setModalType("update");
    } catch (err) {
      console.error(err);
      alert(err.response?.data?.message || "Unable to load contact for editing.");
    }
  };

  const openDeleteModal = (contact) => {
    setSelectedContact(contact);
    setModalType("delete");
  };

  const closeModal = () => {
    setModalType(null);
    setSelectedContact(null);
  };

  const handleFieldChange = (e, field) => {
    setFormData({ ...formData, [field]: e.target.value });
  };

  const handleArrayChange = (index, arrayName, field, value) => {
    const newArray = [...formData[arrayName]];
    newArray[index][field] = value;
    setFormData({ ...formData, [arrayName]: newArray });
  };

  const addArrayItem = (arrayName, defaultObj) => {
    setFormData({ ...formData, [arrayName]: [...formData[arrayName], defaultObj] });
  };

  const removeArrayItem = (index, arrayName) => {
    const newArray = [...formData[arrayName]];
    newArray.splice(index, 1);
    setFormData({ ...formData, [arrayName]: newArray });
  };

  const sanitizeContactPayload = () => {
    const emails = (formData.emails || [])
      .map((item) => ({
        emailAddress: item.emailAddress?.trim() || "",
        label: item.label?.trim() || "Personal",
      }))
      .filter((item) => item.emailAddress !== "");

    const phones = (formData.phones || [])
      .map((item) => ({
        phoneNumber: item.phoneNumber?.trim() || "",
        label: item.label?.trim() || "Personal",
      }))
      .filter((item) => item.phoneNumber !== "");

    return {
      firstName: formData.firstName?.trim() || "",
      lastName: formData.lastName?.trim() || "",
      title: formData.title?.trim() || "Mr.",
      emails,
      phones,
    };
  };

  const handleSubmitContact = async (e) => {
    e.preventDefault();
    const payload = sanitizeContactPayload();
    try {
      if (modalType === "create") {
        await contactService.createContact(payload);
      } else if (modalType === "update") {
        await contactService.updateContact(selectedContact.id, payload);
      }
      closeModal();
      fetchContacts();
    } catch (err) {
      alert(err.response?.data?.message || "Error saving contact.");
      console.error(err);
    }
  };

  const handleDeleteContact = async () => {
    try {
      await contactService.deleteContact(selectedContact.id);
      closeModal();
      fetchContacts();
    } catch (err) {
      alert(err.response?.data?.message || "Error deleting contact.");
      console.error(err);
    }
  };

  const handleExport = async () => {
    try {
      const response = await contactService.exportContacts();
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", "contacts.csv");
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (err) {
      console.error(err);
      alert(err.response?.data?.message || "Export failed.");
    }
  };

  const handleImportFile = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    const importFormData = new FormData();
    importFormData.append("file", file);
    try {
      await contactService.importContacts(importFormData);
      fetchContacts();
      alert("Import successful!");
    } catch (err) {
      console.error(err);
      alert(err.response?.data?.message || "Import failed.");
    }
    e.target.value = null;
  };

  return (
    <div className="animate-fade-in">
      <div className="contact-actions-bar">
        <div className="search-input">
          <Search className="search-icon" size={18} />
          <input
            type="text"
            className="form-control"
            placeholder="Search by first or last name..."
            value={search}
            onChange={handleSearch}
          />
        </div>
        <div style={{ display: "flex", gap: "0.5rem" }}>
          <input
            type="file"
            accept=".csv"
            style={{ display: "none" }}
            ref={fileInputRef}
            onChange={handleImportFile}
          />
          <button className="btn btn-outline" onClick={() => fileInputRef.current.click()}>
            <Upload size={18} /> Import
          </button>
          <button className="btn btn-outline" onClick={handleExport}>
            <Download size={18} /> Export
          </button>
          <button className="btn btn-primary" onClick={openCreateModal}>
            <Plus size={18} /> New Contact
          </button>
        </div>
      </div>

      <div className="glass-panel table-wrapper">
        <table className="contact-table">
          <thead>
            <tr>
              <th>Name</th>
              <th>Title</th>
              <th>Primary Email</th>
              <th>Primary Phone</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr><td colSpan="5" style={{ textAlign: "center" }}>Loading contacts...</td></tr>
            ) : contacts.length === 0 ? (
              <tr><td colSpan="5" style={{ textAlign: "center" }}>No contacts found.</td></tr>
            ) : (
              contacts.map((contact) => (
                <tr key={contact.id}>
                  <td>{contact.firstName} {contact.lastName}</td>
                  <td>{contact.title}</td>
                  <td>{contact.emails?.[0]?.emailAddress || "-"}</td>
                  <td>{contact.phones?.[0]?.phoneNumber || "-"}</td>
                  <td>
                    <div className="action-buttons">
                      <button className="icon-btn" onClick={() => openUpdateModal(contact)} title="Update">
                        <Edit2 size={18} />
                      </button>
                      <button className="icon-btn" onClick={() => navigate(`/contacts/${contact.id}`)} title="View Details">
                        <Eye size={18} />
                      </button>
                      <button className="icon-btn" style={{ color: "var(--danger)" }} onClick={() => openDeleteModal(contact)} title="Delete">
                        <Trash2 size={18} />
                      </button>
                    </div>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {totalPages > 1 && (
        <div className="pagination">
          <button className="btn btn-outline" disabled={page === 0} onClick={() => setPage(page - 1)}>
            <ChevronLeft size={18} />
          </button>
          <span>Page {page + 1} of {totalPages}</span>
          <button className="btn btn-outline" disabled={page === totalPages - 1} onClick={() => setPage(page + 1)}>
            <ChevronRight size={18} />
          </button>
        </div>
      )}

      {modalType === "delete" && (
        <div className="modal-overlay">
          <div className="modal-content glass-panel animate-fade-in" style={{ maxWidth: "400px" }}>
            <div className="modal-header">
              <h3>Delete Contact</h3>
              <button className="close-btn" onClick={closeModal}><X size={20} /></button>
            </div>
            <p>Are you sure you want to delete {selectedContact?.firstName} {selectedContact?.lastName}? This action cannot be undone.</p>
            <div className="modal-actions">
              <button className="btn btn-outline" onClick={closeModal}>Cancel</button>
              <button className="btn btn-danger" onClick={handleDeleteContact}>Confirm Delete</button>
            </div>
          </div>
        </div>
      )}

      {(modalType === "create" || modalType === "update") && (
        <div className="modal-overlay">
          <div className="modal-content glass-panel animate-fade-in" style={{ maxWidth: "700px" }}>
            <div className="modal-header">
              <h3>{modalType === "create" ? "Create New Contact" : "Update Contact"}</h3>
              <button className="close-btn" onClick={closeModal}><X size={20} /></button>
            </div>
            <form onSubmit={handleSubmitContact}>
              <div style={{ display: "flex", gap: "1rem" }}>
                <div className="form-group" style={{ flex: 1 }}>
                  <label className="form-label">Title</label>
                  <select className="form-control" value={formData.title} onChange={(e) => handleFieldChange(e, "title")}>
                    <option value="Mr.">Mr.</option>
                    <option value="Ms.">Ms.</option>
                    <option value="Mrs.">Mrs.</option>
                    <option value="Dr.">Dr.</option>
                    <option value="Prof.">Prof.</option>
                  </select>
                </div>
                <div className="form-group" style={{ flex: 2 }}>
                  <label className="form-label">First Name *</label>
                  <input required className="form-control" value={formData.firstName} onChange={(e) => handleFieldChange(e, "firstName")} />
                </div>
                <div className="form-group" style={{ flex: 2 }}>
                  <label className="form-label">Last Name *</label>
                  <input required className="form-control" value={formData.lastName} onChange={(e) => handleFieldChange(e, "lastName")} />
                </div>
              </div>

              <hr style={{ borderTop: "1px solid var(--glass-border)", margin: "1.5rem 0" }} />

              <h4><Mail size={18} style={{ verticalAlign: "text-bottom", marginRight: "8px" }} /> Email Addresses</h4>
              {formData.emails.map((email, index) => (
                <div className="entry-row" key={`email-${index}`}>
                  <div className="form-group entry-row-flex">
                    <label className="form-label">Email</label>
                    <input type="email" className="form-control" value={email.emailAddress} onChange={(e) => handleArrayChange(index, "emails", "emailAddress", e.target.value)} />
                  </div>
                  <div className="form-group" style={{ width: "30%" }}>
                    <label className="form-label">Label</label>
                    <input type="text" className="form-control" value={email.label} onChange={(e) => handleArrayChange(index, "emails", "label", e.target.value)} />
                  </div>
                  {formData.emails.length > 1 && (
                    <button type="button" className="icon-btn remove-btn" style={{ color: "var(--danger)" }} onClick={() => removeArrayItem(index, "emails")}><Trash2 size={20} /></button>
                  )}
                </div>
              ))}
              <button type="button" className="btn btn-outline" style={{ padding: "0.25rem 0.75rem", fontSize: "0.875rem" }} onClick={() => addArrayItem("emails", { emailAddress: "", label: "Work" })}>+ Add Email</button>

              <hr style={{ borderTop: "1px solid var(--glass-border)", margin: "1.5rem 0" }} />

              <h4><Phone size={18} style={{ verticalAlign: "text-bottom", marginRight: "8px" }} /> Phone Numbers</h4>
              {formData.phones.map((phone, index) => (
                <div className="entry-row" key={`phone-${index}`}>
                  <div className="form-group entry-row-flex">
                    <label className="form-label">Phone</label>
                    <input type="text" className="form-control" value={phone.phoneNumber} onChange={(e) => handleArrayChange(index, "phones", "phoneNumber", e.target.value)} />
                  </div>
                  <div className="form-group" style={{ width: "30%" }}>
                    <label className="form-label">Label</label>
                    <input type="text" className="form-control" value={phone.label} onChange={(e) => handleArrayChange(index, "phones", "label", e.target.value)} />
                  </div>
                  {formData.phones.length > 1 && (
                    <button type="button" className="icon-btn remove-btn" style={{ color: "var(--danger)" }} onClick={() => removeArrayItem(index, "phones")}><Trash2 size={20} /></button>
                  )}
                </div>
              ))}
              <button type="button" className="btn btn-outline" style={{ padding: "0.25rem 0.75rem", fontSize: "0.875rem" }} onClick={() => addArrayItem("phones", { phoneNumber: "", label: "Mobile" })}>+ Add Phone</button>

              <div className="modal-actions">
                <button type="button" className="btn btn-outline" onClick={closeModal}>Cancel</button>
                <button type="submit" className="btn btn-primary">{modalType === "update" ? "Update Contact" : "Save Details"}</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default Dashboard;
