# MySQL-specific code to create tables for auction server.


# as_currencies contains currencies supported by auction server.
CREATE TABLE as_currencies (
  id         INTEGER,                       # Currency id.
  name       VARCHAR(3)       NOT NULL,     # USD, CAD, NZD, etc.
  PRIMARY KEY (id)
);

# Insert USD as initial currency.
INSERT INTO as_currencies values(1, 'USD');


# as_site contains details about this auction server installation.
CREATE TABLE as_site (
  uuid           CHAR(36)         NOT NULL,     # Random UUID identifying this auction server.
  site_name      VARCHAR(64)      NOT NULL,     # Name for this server.
  uri            VARCHAR(256),                  # URI at which the server is available to users.
  hub_uuid       CHAR(36),                      # Optional primary hub UUID to connect to.
  hub_uri        VARCHAR(256),                  # Primary hub URI.
  alt_hub_uuid   CHAR(36),                      # Optional secondary hub UUID to connect to.
  alt_hub_uri    VARCHAR(256),                  # Secondary hub URI.
);


# as_users contains users registered with this server.
CREATE TABLE as_users (
  uuid       CHAR(36)         NOT NULL,     # Random UUID identifying a specific user.
  login      VARCHAR(32)      NOT NULL,     # User login.
  password   CHAR(32)         NOT NULL,     # Password hash.
  name       VARCHAR(64)      NOT NULL,     # User name.
  PRIMARY KEY (login)
);


# as_local_auctions contains active auctions originated on this server by locally registered users.
# Note that as auctions are closed, they are moved to as_closed_auctions to keep this table small.
CREATE TABLE as_local_auctions (
  uuid                  CHAR(36)     NOT NULL,  # Random UUID of the auction, generated by the site.
  seller_uuid           CHAR(36)     NOT NULL,  # Seller UUID.
  name                  VARCHAR(256) NOT NULL,  # Name of the auction item.
  description           TEXT         NOT NULL,  # Description of the auction item.
  image_uri             VARCHAR(256),           # Image URI.
  created_timestamp     CHAR(19)     NOT NULL,  # Creation timestamp in format like "2016-04-08 15:00:10".
  close_timestamp       CHAR(19)     NOT NULL,  # Close timestamp in format like "2016-04-15 15:00:10".
  currency_id           INTEGER      NOT NULL,  # Currency id.
  reserve_price         NUMERIC(15,2),          # Reserve price of the auction.
  current_price         NUMERIC(15,2),          # Current price of the auction.
  status                INTEGER,                # Status of the auction.
  PRIMARY KEY (uuid)
);


# as_remote_auctions contains active auctions originated on remote servers.
CREATE TABLE as_remote_auctions (
  uuid                  CHAR(36)     NOT NULL,  # Random UUID of the auction, generated by the site.
  site_uuid             CHAR(36)     NOT NULL,  # Originating site UUID.
  hub_uuid              CHAR(36)     NOT NULL,  # Originating site primary hub UUID.
  seller_uuid           CHAR(36)     NOT NULL,  # Seller UUID.
  name                  VARCHAR(256) NOT NULL,  # Name of the auction item.
  description           TEXT         NOT NULL,  # Description of the auction item.
  image_uri             VARCHAR(256),           # Image URI.
  created_timestamp     CHAR(19)     NOT NULL,  # Creation timestamp in format like "2016-04-08 15:00:10".
  close_timestamp       CHAR(19)     NOT NULL,  # Close timestamp in format like "2016-04-15 15:00:10".
  currency_id           INTEGER      NOT NULL,  # Currency id.
  reserve_price         NUMERIC(15,2),          # Reserve price of the auction.
  current_price         NUMERIC(15,2),          # Current price of the auction.
  status                INTEGER,                # Status of the auction.
  PRIMARY KEY (uuid)
);


# as_closed_auctions contains closed auctions originated on this server.
CREATE TABLE as_closed_auctions (
  uuid                  CHAR(36)     NOT NULL,  # Random UUID of the auction, generated by the site.
  seller_uuid           CHAR(36)     NOT NULL,  # Seller UUID.
  name                  VARCHAR(256) NOT NULL,  # Name of the auction item.
  description           TEXT         NOT NULL,  # Description of the auction item.
  image_uri             VARCHAR(256),           # Image URI.
  created_timestamp     CHAR(19)     NOT NULL,  # Creation timestamp in format like "2016-04-08 15:00:10".
  close_timestamp       CHAR(19)     NOT NULL,  # Close timestamp in format like "2016-04-15 15:00:10".
  currency_id           INTEGER      NOT NULL,  # Currency id.
  reserve_price         NUMERIC(15,2),          # Reserve price of the auction.
  current_price         NUMERIC(15,2),          # Current price of the auction.
  status                INTEGER,                # Status of the auction.
  PRIMARY KEY (uuid)
);


# as_bids contains bids created by local users on local and remote items.
CREATE TABLE as_bids (
  uuid                  CHAR(36)     NOT NULL,   # Random UUID of the bid.
  site_uuid             CHAR(36),                # Site UUID for the auction item.
  auction_uuid          CHAR(36)     NOT NULL,   # UUID of the auction item.
  max_price             NUMERIC(15,2),           # Max price of the pid.
  user_uuid             CHAR(36)     NOT NULL,   # User UUID who placed the bid.
  created_timestamp     CHAR(19)     NOT NULL,   # Bid creation timestamp in format like "2016-04-08 15:01:10".
  status                INTEGER,                 # Status of the bid.
  PRIMARY KEY (uuid)
);


# as_closed_bids contains closed bids created by local users on local and remote items.
CREATE TABLE as_closed_bids (
  uuid                  CHAR(36)     NOT NULL,   # Random UUID of the bid.
  site_uuid             CHAR(36),                # Site UUID for the auction item.
  auction_uuid          CHAR(36)     NOT NULL,   # UUID of the auction item.
  max_price             NUMERIC(15,2),           # Max price of the pid.
  user_uuid             CHAR(36)     NOT NULL,   # User UUID who placed the bid.
  created_timestamp     CHAR(19)     NOT NULL,   # Bid creation timestamp in format like "2016-04-08 15:01:10".
  status                INTEGER,                 # Status of the bid.
  PRIMARY KEY (uuid)
);

