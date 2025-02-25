-- tao database
CREATE DATABASE shopApp;
use  shopApp;
-- bảng users -> KH muốn mua hàng thì phải đăng ký TK.
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    fullname VARCHAR(100) DEFAULT '',
    phone_number VARCHAR(10) NOT NULL,
    address VARCHAR(200) DEFAULT '',
    password VARCHAR(100) NOT NULL DEFAULT '', -- mật khẩu đã mã hóa
    created_at DATETIME,
    updated_at DATETIME,
    is_active TINYINT(1) DEFAULT 1,
    date_of_birth DATE,
    facebook_account_id INT DEFAULT 0,
    google_account_id INT DEFAULT 0
);
-- alter trường vào bảng users.
alter table users add COLUMN role_id INT;

-- bảng phân quyền
CREATE TABLE roles (
	id INT PRIMARY KEY AUTO_INCREMENT,
	name VARCHAR(20) NOT NULL
);
-- alter khoá ngoại cho bảng users vs roles
alter table users add FOREIGN KEY (role_id) REFERENCES roles(id);

-- bảng tokens lưu trữ (authentication, security, session managment, ....)
CREATE TABLE tokens (
	id INT PRIMARY KEY AUTO_INCREMENT,
	token VARCHAR(255) UNIQUE NOT NULL,
	token_type VARCHAR(50) NOT NULL,
	expiration_date DATETIME,
	revoked TINYINT(1) NOT NULL,
	expired TINYINT(1) NOT NULL,
	user_id INT,
	FOREIGN KEY (user_id) REFERENCES users(id)
);

-- hỗ trợ đăng nhập từ Face vs Google 
CREATE TABLE social_accounts (
	id INT PRIMARY KEY AUTO_INCREMENT,
	provider VARCHAR(20) NOT NULL COMMENT 'Tên nhà social network',
	provider_id VARCHAR(50) NOT NULL,
	email VARCHAR(150) NOT NULL COMMENT 'Email tài khoản',
	name VARCHAR(100) NOT NULL COMMENT 'Tên người dùng',
	user_id INT,
	FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Bảng danh mục sản phẩm
CREATE TABLE categories (
	id INT PRIMARY KEY AUTO_INCREMENT,
	name VARCHAR(100) NOT NULL DEFAULT '' COMMENT 'Tên danh mục, vd: iphone,...'
);

-- bảng chứa sản phẩm
CREATE TABLE products (
	id INT PRIMARY KEY AUTO_INCREMENT,
	name VARCHAR(350) COMMENT 'Tên sản phẩm',
	price FLOAT NOT NULL CHECK(price >= 0),
	thumbnail VARCHAR(350) DEFAULT '' COMMENT 'Đường dẫn url..',
	descriptions LONGTEXT DEFAULT '',
	created_at DATETIME,
    updated_at DATETIME,
	category_id INT,
	FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE product_images (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,
  product_id INT,
  image_url VARCHAR(300),
  FOREIGN KEY (product_id) REFERENCES products(id),
  CONSTRAINT FK_product_images_product_id FOREIGN KEY (product_id) REFERENCES products(id)ON DELETE CASCADE
);
# drop table product_images;
-- bảng đặt hàng 
CREATE TABLE orders (
	id INT PRIMARY KEY AUTO_INCREMENT,
	user_id INT NOT NULL,
	full_name VARCHAR(100) DEFAULT '' COMMENT 'Tên khách hàng',
	email VARCHAR(100) DEFAULT '' COMMENT 'Email khách hàng',
	phone_number VARCHAR(20) NOT NULL COMMENT 'SĐT khách hàng',
	address VARCHAR(300) NOT NULL COMMENT 'Địa chỉ khách hàng',
	note VARCHAR(100) DEFAULT '',
	order_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT '',
	total_money FLOAT check(total_money >= 0),
	FOREIGN KEY (user_id) REFERENCES users(id) -- một user có thể muua nhiều đơn hàng
);
-- alter thêm trường vào bảng orders 
alter table orders add COLUMN shipping_method VARCHAR(100);
alter table orders add COLUMN shipping_address VARCHAR(200);
alter table orders add COLUMN shipping_date DATETIME;
alter table orders add COLUMN tracking_number VARCHAR(100);
alter table orders add COLUMN payment_method VARCHAR(100);
-- cập nhật trạng thái đơn hàng khi xoá mềm
alter table orders add COLUMN active TINYINT(1);
-- Trạng thái đơn hàng chỉ được phép nhận "một số giá trị cụ thể"
alter table orders MODIFY COLUMN status ENUM('pending', 'processing', 'shipped', 'delivered', 'cancelled') COMMENT 'Trạng thái đơn hàng';
-- bảng chi tiết đơn hàng
CREATE TABLE order_details (
	id INT PRIMARY KEY AUTO_INCREMENT,
	order_id INT NOT NULL,
	product_id INT NOT NULL,
	price FLOAT check(total_money >= 0),
	number_of_products INT check(number_of_products > 0),
	total_money FLOAT check(total_money >= 0),
	color VARCHAR(20) DEFAULT '',
	FOREIGN KEY (order_id) REFERENCES orders(id),
	FOREIGN KEY (product_id) REFERENCES products(id) 
);