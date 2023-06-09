DROP TABLE IF EXISTS users, items, bookings, comments, requests;

CREATE TABLE users (
    id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    email VARCHAR(320) NOT NULL,
	CONSTRAINT uc_users_email UNIQUE (email)
);

CREATE TABLE requests (
    id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    description VARCHAR(100) NOT NULL,
    requestor_id bigint NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT fk_requests_requestor_id FOREIGN KEY(requestor_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE items (
    id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(30)   NOT NULL,
    description VARCHAR(100)   NOT NULL,
    is_available boolean NOT NULL,
    owner_id bigint NOT NULL,
    request_id bigint,
    CONSTRAINT fk_items_owner_id FOREIGN KEY(owner_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_requests_request_id FOREIGN KEY(request_id) REFERENCES requests (id) ON DELETE CASCADE
);

CREATE TABLE bookings (
    id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id bigint NOT NULL,
    booker_id bigint NOT NULL,
    status varchar NOT NULL,
    CONSTRAINT fk_bookings_item_id FOREIGN KEY(item_id) REFERENCES items (id) ON DELETE CASCADE,
    CONSTRAINT fk_bookings_booker_id FOREIGN KEY(booker_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE comments (
    id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    text VARCHAR(100)   NOT NULL,
    item_id bigint NOT NULL,
    author_name varchar NOT NULL,
    created bigint  NOT NULL,
    CONSTRAINT fk_comments_item_id FOREIGN KEY(item_id) REFERENCES items (id) ON DELETE CASCADE
);
