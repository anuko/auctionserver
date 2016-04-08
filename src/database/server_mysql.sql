# Some preliminary ideas on database structure...


# as_currencies table contains currencies supported by auction server.
CREATE TABLE as_currencies (
  id         INTEGER,                       # Currency id.
  name       VARCHAR(3)       NOT NULL,     # USD, CAD, NZD, etc.
  PRIMARY KEY (id)
);

# Insert USD as initial currency.
INSERT INTO as_currencies values(1, 'USD');


# as_site table contains details about this auction server installation.
CREATE TABLE as_site (
  uuid       CHAR(36)         NOT NULL,     # Random UUID identifying this auction server.
  sitename   VARCHAR(64)      NOT NULL,     # Name for this server.
  uri        VARCHAR(256)                   # URI at which the server is available to users.
);


/*
CREATE TABLE as_user (
  id                    BIGINT          NOT NULL, // auto-increment?
  username              VARCHAR(64)     NOT NULL, // must be unique per site.
  site_id               BIGINT          NOT NULL
);





CREATE TABLE as_auctions (
  uuid                  CHAR(36)     NOT NULL,   // Random UUID of the auction, generated by the site.
  site_name             VARCHAR(256) NOT NULL,   // URI of the originating auction site.
  seller_uuid           CHAR(36)     NOT NULL,   // Seller UUID.
  name                  VARCHAR(256) NOT NULL,   // Name of the auction item.
  description           TEXT,
  image                 BLOB,
  created_timestamp     timestamp
  close_timestamp       timestamp
  currency              VARCHAR(3)   NOT NULL,    // USD, CAD, NZD, EUT, YEN, GPB, etc.
  reserve_price         numeric
  current_price         numeric,
  status                INTEGER
);

CREATE TABLE as_closed_auctions (
  uuid                  CHAR(36)     NOT NULL,   // Random UUID of the auction, generated by the site.
  site_uuid             CHAR(36)     NOT NULL,   // Originating auction site UUID.
  seller_uuid           CHAR(36)     NOT NULL,   // Seller UUID.
  description           TEXT,
  image                 BLOB,
  created_timestamp     timestamp
  close_timestamp       timestamp
  currency              VARCHAR(3)   NOT NULL,    // USD, CAD, NZD, EUT, YEN, GPB, etc.
  reserve_price         numeric
  current_price         numeric,
  status                INTEGER
);

CREATE TABLE as_bids (
  uuid                  CHAR(36)     NOT NULL,   // Random UUID of the bid.
  auction_uuid          CHAR(36)     NOT NULL,   // UUID of the auction from the aaAuctions table.
  max_price             numeric
  user_uuid             CHAR(36)     NOT NULL,   //
  site_name
  created_timestamp     timestamp
  status
);

CREATE TABLE as_closed_bids (
  uuid                  CHAR(36)     NOT NULL,   // Random UUID of the bid.
  auction_uuid          CHAR(36)     NOT NULL,   // UUID of the auction from the aaAuctions table.
  max_price             numeric
  user_uuid             CHAR(36)     NOT NULL,   //
  site_name
  created_timestamp     timestamp
  status
);

CREATE TABLE as_auction_sites (
  uuid                  CHAR(36)     NOT NULL,   // Random UUID of the auction, generated by the site.
  site_name             VARCHAR(64)  NOT NULL,   // Name the originating auction site.
  site_uri              VARCHAR(256) NOT NULL,   // URI of the originating auction site.
  status                INTEGER
);
*/
