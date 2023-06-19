package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.Collection;

import static ru.practicum.shareit.util.Util.X_SHARER_USER_ID;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto createBooking(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                    @RequestBody @Validated(BookingBasicInfo.class) BookingDto dto) {
        return service.create(userId, dto);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto updateBooking(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                    @PathVariable Long bookingId,
                                    @RequestParam(name = "approved") Boolean approved) {
        return service.update(userId, bookingId, approved);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<BookingDto> getBookings(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                              @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return service.findAll(userId, state);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public Collection<BookingDto> getBookingsForOwner(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                      @RequestParam(name = "state",
                                                              defaultValue = "ALL") String state) {
        return service.findAllForOwner(userId, state);
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto getBookingById(@RequestHeader(X_SHARER_USER_ID) Long userId, @PathVariable Long bookingId) {
        return service.findById(userId, bookingId);
    }
}
