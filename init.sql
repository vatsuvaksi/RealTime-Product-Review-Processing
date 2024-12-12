CREATE TABLE product_review (
    review_id SERIAL PRIMARY KEY,                 -- Unique identifier for each review
    product_id INT NOT NULL,                     -- Reference to the product (foreign key to product table)
    user_id INT NOT NULL,                        -- Identifier for the user who made the review
    rating SMALLINT CHECK (rating BETWEEN 1 AND 5), -- Rating between 1 and 5
    review_text TEXT,                            -- Text of the review
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Timestamp for when the review was created
    is_processed BOOLEAN DEFAULT FALSE           -- Flag to indicate if the review has been processed
);

CREATE INDEX idx_created_at ON product_review (created_at);