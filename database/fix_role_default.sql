-- Add default value to role/typeuser column
ALTER TABLE user MODIFY COLUMN typeuser ENUM('admin', 'player') DEFAULT 'player';

-- Also update approval_status if it exists
ALTER TABLE user MODIFY COLUMN approval_status VARCHAR(50) DEFAULT 'pending';
