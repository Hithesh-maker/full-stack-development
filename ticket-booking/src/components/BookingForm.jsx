import { useState } from "react";

function BookingForm({ event, onBooking }) {
  const [formData, setFormData] = useState({
    name: "",
    email: "",
    department: "",
    tickets: ""
  });

  const [errors, setErrors] = useState({});

  const handleChange = (e) => {
    const { name, value } = e.target;

    setFormData({
      ...formData,
      [name]: value
    });
  };

  const validateForm = () => {
    const newErrors = {};

    if (!formData.name.trim()) {
      newErrors.name = "Name is required";
    }

    if (!formData.email.trim()) {
      newErrors.email = "Email ID is required";
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
      newErrors.email = "Enter a valid email ID";
    }

    if (!formData.department.trim()) {
      newErrors.department = "Department is required";
    }

    if (!formData.tickets) {
      newErrors.tickets = "Number of tickets is required";
    } else if (Number(formData.tickets) <= 0) {
      newErrors.tickets = "Tickets must be a positive number";
    } else if (Number(formData.tickets) > event.availableTickets) {
      newErrors.tickets = `Only ${event.availableTickets} tickets are available`;
    }

    setErrors(newErrors);

    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    onBooking(formData);

    setFormData({
      name: "",
      email: "",
      department: "",
      tickets: ""
    });

    setErrors({});
  };

  const handleReset = () => {
    setFormData({
      name: "",
      email: "",
      department: "",
      tickets: ""
    });

    setErrors({});
  };

  return (
    <div className="booking-card">
      <h2>Book Your Tickets</h2>

      <form onSubmit={handleSubmit}>

        <label>Name</label>
        <input
          type="text"
          name="name"
          value={formData.name}
          onChange={handleChange}
          placeholder="Enter your name"
        />
        {errors.name && <p className="error">{errors.name}</p>}

        <label>Email ID</label>
        <input
          type="email"
          name="email"
          value={formData.email}
          onChange={handleChange}
          placeholder="Enter your email"
        />
        {errors.email && <p className="error">{errors.email}</p>}

        <label>Department</label>
        <input
          type="text"
          name="department"
          value={formData.department}
          onChange={handleChange}
          placeholder="Enter your department"
        />
        {errors.department && (
          <p className="error">{errors.department}</p>
        )}

        <label>Number of Tickets</label>
        <input
          type="number"
          name="tickets"
          value={formData.tickets}
          onChange={handleChange}
          min="1"
          placeholder="Enter number of tickets"
        />
        {errors.tickets && (
          <p className="error">{errors.tickets}</p>
        )}

        <div className="button-group">
          <button type="submit">
            Book Tickets
          </button>

          <button
            type="button"
            className="reset-btn"
            onClick={handleReset}
          >
            Reset
          </button>
        </div>

      </form>
    </div>
  );
}

export default BookingForm;