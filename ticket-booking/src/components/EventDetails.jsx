function EventDetails({ event }) {
  return (
    <div className="event-card">
      <h2>Event Details</h2>

      <div className="event-info">
        <p>
          <strong>Event Name:</strong>
          <span>{event.name}</span>
        </p>

        <p>
          <strong>Department:</strong>
          <span>{event.department}</span>
        </p>

        <p>
          <strong>Date & Time:</strong>
          <span>{event.dateTime}</span>
        </p>

        <p>
          <strong>Venue:</strong>
          <span>{event.venue}</span>
        </p>

        <p>
          <strong>Ticket Price:</strong>
          <span>₹{event.price}</span>
        </p>

        <p className="available">
          <strong>Available Tickets:</strong>
          <span>{event.availableTickets}</span>
        </p>
      </div>
    </div>
  );
}

export default EventDetails;