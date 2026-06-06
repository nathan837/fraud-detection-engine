INSERT INTO rules (name, description, condition_type, condition_value, score, is_active)
VALUES
(
    'impossible_travel',
    'User login from 500km+ away in under 60 minutes',
    'impossible_travel',
    '{"threshold_km": 500, "window_minutes": 60}',
    50,
    true
),
(
    'new_device',
    'Transaction from a device never seen before',
    'new_device',
    '{}',
    20,
    true
),
(
    'high_velocity',
    'More than 5 transactions in 60 seconds',
    'velocity_breach',
    '{"max_count": 5, "window_seconds": 60}',
    30,
    true
),
(
    'large_amount',
    'Transaction above 50000 ETB',
    'amount_threshold',
    '{"threshold": 50000, "currency": "ETB"}',
    25,
    true
),
(
    'new_recipient',
    'Sending money to a recipient never used before',
    'new_recipient',
    '{}',
    15,
    true
);