# MySQL-specific code to create tables for auction server.


# as_site_details contains details about this installation.
CREATE TABLE as_site_details (
  uuid               CHAR(36)       NOT NULL,   # UUID identifying this auction site.
  name               VARCHAR(64)    NOT NULL,   # Name for this site.
  uri                VARCHAR(256),              # URI at which the server is available to its users.
  email              VARCHAR(256),              # Email of site admin for notifications.
  language           VARCHAR(5),                # Language of the site, such as en, or pt-BR.
  template           VARCHAR(64),               # Website design template.
  tracker_conf       VARCHAR(64)                # Path to visitor tracker config file, or NULL for no tracking.
);


# as_users contains users registered with this site.
CREATE TABLE as_users (
  uuid               CHAR(36)       NOT NULL,   # User UUID.
  login              VARCHAR(32)    NOT NULL,   # User login.
  password           CHAR(32)       NOT NULL,   # Password hash.
  name               VARCHAR(64)    NOT NULL,   # User name.
  email              VARCHAR(64)    NOT NULL,   # User email.
  confirmed          INTEGER        NOT NULL DEFAULT 0, # Whether user email is confirmed.
  status             INTEGER,                   # User status.
  PRIMARY KEY (login)
);


# as_currencies contains currency symbols supported by this site.
CREATE TABLE as_currencies (
  currency           CHAR(3)        NOT NULL,   # Currency symbol: USD, CAD, NZD, etc.
  checkout_email     VARCHAR(64),               # Checkout email.
  ord_num            INTEGER,                   # Ordering number for currency list.
  PRIMARY KEY (currency)
);

# as_durations contains possible auction duration intervals in days supported by this site.
CREATE TABLE as_durations (
  duration           INTEGER        NOT NULL,   # Auction duration in days.
  ord_num            INTEGER,                   # Ordering number for duration dropdown.
  PRIMARY KEY (duration)
);

# Insert some durations.
insert into as_durations values(30, 0);
insert into as_durations values(10, 10);
insert into as_durations values(7, 20);
insert into as_durations values(3, 30);


# as_items contains auction items known to this site (local and remote).
CREATE TABLE as_items (
  uuid               CHAR(36)       NOT NULL,   # UUID of the item, generated by the site.
  origin             CHAR(36)       NOT NULL,   # Originating node UUID.
  seller_uuid        CHAR(36)       NOT NULL,   # Seller UUID.
  name               VARCHAR(256)   NOT NULL,   # Name of the auction item.
  description        TEXT,                      # Description of the auction item.
  image_uri          VARCHAR(256),              # Image URI.
  created_timestamp  CHAR(19)       NOT NULL,   # Creation timestamp in format like "2016-04-08 15:00:10".
  close_timestamp    CHAR(19)       NOT NULL,   # Close timestamp in format like "2016-04-15 15:00:10".
  currency           CHAR(3)        NOT NULL,   # Currency: USD, CAD, NZD, etc.
  reserve_price      NUMERIC(15,2),             # Reserve price of the item.
  bids               INTEGER,                   # Number of bids on the item.
  top_bid            NUMERIC(15,2),             # Current top bid for the item.
  top_bid_uuid       CHAR(36),                  # Current top bid UUID.
  approved           INTEGER,                   # Whether the item is approved for site. 1 - approved, 0 - disapproved, NULL - not reviewed.
  processed          INTEGER        NOT NULL DEFAULT 0, # Whether the item is processed by this server. 0 means "not processed".
  reminder_sent      INTEGER        NOT NULL DEFAULT 0, # Whether a 24-hour closing reminder is sent to all losing bidders.
  status             INTEGER,                   # Auction status. 1 - active, 0 - closed, NULL - deleted.
  PRIMARY KEY (uuid)
);
# TODO: add indexes.


# as_bids contains bids known to ths server.
CREATE TABLE as_bids (
  uuid               CHAR(36)       NOT NULL,   # UUID of the bid.
  origin             CHAR(36),                  # Site UUID for the bid origin.
  item_uuid          CHAR(36)       NOT NULL,   # UUID of the auction item.
  item_origin        CHAR(36),                  # Site UUID for item origin.
  amount             NUMERIC(15,2),             # Amount of the pid.
  user_uuid          CHAR(36)       NOT NULL,   # User UUID who placed the bid.
  created_timestamp  CHAR(19)       NOT NULL,   # Bid creation timestamp in format like "2016-04-08 15:01:10".
  confirmed          INTEGER,                   # Whether the bid is confirmed by user.
  processed          INTEGER        NOT NULL DEFAULT 0, # Whether the bid is processed by this server.
  status             INTEGER,                   # Status of the bid. 0 - lost, 1 - potentially winning.
  PRIMARY KEY (uuid)
);
# TODO: add indexes.


# as_tmp_refs contains temporary references for user confirmations and password resets.
CREATE TABLE as_tmp_refs (
  uuid               CHAR(36)       NOT NULL,   # UUID of the reference, our random code.
  user_uuid          CHAR(36)       NOT NULL,   # User UUID for whom the reference is for.
  bid_uuid           CHAR(36),                  # Bid UUID for which the reference is for.
  created_timestamp  CHAR(19)       NOT NULL,   # Reference creation timestamp in format like "2016-04-18 15:00:00".
  PRIMARY KEY (uuid)
);