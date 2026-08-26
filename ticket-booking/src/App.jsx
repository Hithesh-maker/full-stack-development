import { useState } from "react";
import EventDetails from "./components/EventDetails";
import BookingForm from "./components/BookingForm";
import "./App.css";

function App() {
  const [event, setEvent] = useState({
    name: "VTR UGE - 2021 – B.Tech - CSE",
    department: "Department of Computer Science and Engineering",
    dateTime: "15 September 2026, 10:00 AM",
    venue: "Main Seminar Hall",
    price: 100,
    availableTickets: 100,
  });

  const [booking, setBooking] = useState(null);

  const handleBooking = (formData) => {
    const ticketsBooked = Number(formData.tickets);

    setEvent((previousEvent) => ({
      ...previousEvent,
      availableTickets:
        previousEvent.availableTickets - ticketsBooked,
    }));

    setBooking({
      bookingId:
        "CSE" + Math.floor(1000 + Math.random() * 9000),
      name: formData.name,
      email: formData.email,
      department: formData.department,
      tickets: ticketsBooked,
      totalAmount: ticketsBooked * event.price,
    });
  };

  return (
    <div className="app">
      <header>
        <h1>Internal Department Event</h1>
        <p>Online Ticket Booking System</p>
      </header>

      <main>
        <EventDetails event={event} />

        <BookingForm
          event={event}
          onBooking={handleBooking}
        />

        {booking && (
          <div className="confirmation">
            <h2>Booking Confirmed!</h2>

            <p>
              <strong>Booking ID:</strong> {booking.bookingId}
            </p>

            <p>
              <strong>User Name:</strong> {booking.name}
            </p>

            <p>
              <strong>Event Name:</strong> {event.name}
            </p>

            <p>
              <strong>Tickets Booked:</strong> {booking.tickets}
            </p>

            <p>
              <strong>Total Amount:</strong> ₹{booking.totalAmount}
            </p>

            <p className="success">
              Your tickets have been successfully booked.
            </p>
          </div>
        )}
      </main>

      <footer>
        <p>Department of Computer Science and Engineering</p>
      </footer>
    </div>
  );
}

export default App;