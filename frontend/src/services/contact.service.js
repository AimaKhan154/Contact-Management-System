import api from "./api";

const getAllContacts = (page = 0, size = 10, search = "") => {
  return api.get(`/contacts`, {
    params: { page, size, search },
  });
};

const getContact = (id) => {
  return api.get(`/contacts/${id}`);
};

const createContact = (data) => {
  return api.post("/contacts", data);
};

const updateContact = (id, data) => {
  return api.put(`/contacts/${id}`, data);
};

const deleteContact = (id) => {
  return api.delete(`/contacts/${id}`);
};

const exportContacts = () => {
  return api.get('/contacts/export', { responseType: 'blob' });
};

const importContacts = (formData) => {
  // Let the browser set multipart boundaries automatically.
  return api.post('/contacts/import', formData);
};

export default {
  getAllContacts,
  getContact,
  createContact,
  updateContact,
  deleteContact,
  exportContacts,
  importContacts
};
