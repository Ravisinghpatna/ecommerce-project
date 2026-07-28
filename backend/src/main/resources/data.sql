-- Sirf tab insert karo jab table khaali ho, taaki restart par duplicate rows na banein
INSERT INTO products (name, description,category, price, image_url, stock)
SELECT * FROM (VALUES
    ('Wireless Mouse', 'Ergonomic wireless mouse with USB receiver','Accessories', 799.00, '"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTf6xo3JLLoXAyJE8VqGr9Oirs9xB_t5dhty0it9P81Uw&s=10"', 50),
    ('Mechanical Keyboard', 'RGB backlit mechanical keyboard, blue switches', 'Accessories', 2999.00, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQi8QOAk2A2CQ8FNtq8cxjzBaoLIulb8Oys7COUobP9-Q&s=10', 30),
    ('Noise Cancelling Headphones', 'Over-ear Bluetooth headphones, 30hr battery', 'Audio', 4499.00, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ3s_Ou4ggSRDucDarVCTVbM-YdS3Z9BNWM-S5ds0GCVQ&s=10', 20),
    ('27-inch Monitor', 'Full HD IPS monitor, 75Hz refresh rate', 'Electronics', 12999.00, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSczx0QiANzQN1I9iPHf3y6Ew2ECkR-LeRepnjSoUq5fA&s=10', 15),
    ('USB-C Hub', '7-in-1 USB-C hub with HDMI and card reader', 'Accessories', 1499.00, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSXZMYe4RaQYhT5MJ9y0tV3NOQCO4cHIf3grwf20xrOwg&s=10', 40),
    ('Laptop Stand', 'Adjustable aluminum laptop stand', 'Accessories', 1199.00, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQgTdVA4cR8hFq4Io9QtFJrwiQy9S49Bgs8iiYgI_FDtw&s=10', 25),
	 ('OPPO RENO 12PRO 5G', 'OPPO Reno 12 Pro 5G (Sunset Gold, 256 GB) (12 GB RAM)', 'Electronics', 36999.00, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR_QrgFCF7m9e8eixPkrHF1iW_b98PpaoI5jmO4enCwuQ&s=10', 25),
	  ('Apple iPhone 17 Pro Max', 'The Apple iPhone 17 Pro Max is a flagship smartphone featuring a 6.9-inch Super Retina XDR display', 'Electronics', 141900.00, 'https://jinglestore.ru/d/iphone-17-pro-max-deep-blue-1.png', 99)
) AS v(name, description, category, price, image_url, stock)
WHERE NOT EXISTS (SELECT 1 FROM products);