USE library_management;

-- 清空现有数据（谨慎使用）
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE books;
TRUNCATE TABLE users;
SET FOREIGN_KEY_CHECKS = 1;

-- 插入管理员用户
INSERT INTO users (username, password, is_admin) VALUES
('admin', 'admin123', 1),
('user1', 'password123', 0);

-- 插入计算机类图书 (C001-C005)
INSERT INTO books (isbn, title, author, type, stock, programming_language, framework, difficulty) VALUES
('C001', 'Java编程思想', 'Bruce Eckel', 'Computer', 10, 'Java', 'None', 'Intermediate'),
('C002', 'Spring实战', 'Craig Walls', 'Computer', 8, 'Java', 'Spring', 'Advanced'),
('C003', 'Python深度学习', 'François Chollet', 'Computer', 5, 'Python', 'TensorFlow', 'Advanced'),
('C004', 'JavaScript高级程序设计', 'Matt Frisbie', 'Computer', 12, 'JavaScript', 'React', 'Intermediate'),
('C005', 'C++程序设计语言', 'Bjarne Stroustrup', 'Computer', 6, 'C++', 'STL', 'Advanced');

-- 插入文学类图书 (L001-L005)
INSERT INTO books (isbn, title, author, type, stock, genre, era, language) VALUES
('L001', '红楼梦', '曹雪芹', 'Literature', 15, 'Classical', 'Qing Dynasty', 'Chinese'),
('L002', '战争与和平', '列夫·托尔斯泰', 'Literature', 7, 'Novel', '19th Century', 'Russian'),
('L003', '百年孤独', '加西亚·马尔克斯', 'Literature', 9, 'Magic Realism', '20th Century', 'Spanish'),
('L004', '傲慢与偏见', '简·奥斯汀', 'Literature', 11, 'Romance', '19th Century', 'English'),
('L005', '三国演义', '罗贯中', 'Literature', 13, 'Historical', 'Ming Dynasty', 'Chinese');

-- 插入科学类图书 (S001-S005)
INSERT INTO books (isbn, title, author, type, stock, subject_area, research_field, academic_level) VALUES
('S001', '时间简史', '史蒂芬·霍金', 'Science', 12, 'Physics', 'Cosmology', 'Popular'),
('S002', '基因传', '悉达多·穆克吉', 'Science', 6, 'Biology', 'Genetics', 'Advanced'),
('S003', '元素周期表', '西奥多·格雷', 'Science', 8, 'Chemistry', 'Inorganic', 'Intermediate'),
('S004', '费曼物理学讲义', '理查德·费曼', 'Science', 5, 'Physics', 'General', 'Advanced'),
('S005', '生物多样性', 'Edward O. Wilson', 'Science', 7, 'Biology', 'Ecology', 'Intermediate');

-- 插入艺术类图书 (A001-A005)
INSERT INTO books (isbn, title, author, type, stock, art_form, medium, style) VALUES
('A001', '艺术的故事', '贡布里希', 'Art', 5, 'Visual Art', 'Mixed', 'Historical Survey'),
('A002', '梵高传', '欧文·斯通', 'Art', 7, 'Painting', 'Oil Paint', 'Post-Impressionism'),
('A003', '建筑学十书', '维特鲁威', 'Art', 4, 'Architecture', 'Classical', 'Roman'),
('A004', '现代艺术150年', '威尔·贡培兹', 'Art', 8, 'Modern Art', 'Mixed', 'Contemporary'),
('A005', '中国绘画史', '邹一桂', 'Art', 6, 'Painting', 'Ink', 'Traditional Chinese');

-- 插入历史类图书 (H001-H005)
INSERT INTO books (isbn, title, author, type, stock, time_period, region, historical_figures) VALUES
('H001', '人类简史', '尤瓦尔·赫拉利', 'History', 20, 'All History', 'Global', 'Multiple'),
('H002', '明朝那些事儿', '当年明月', 'History', 15, 'Ming Dynasty', 'China', 'Multiple Emperors'),
('H003', '第二次世界大战史', '温斯顿·丘吉尔', 'History', 8, '1939-1945', 'Global', 'World Leaders'),
('H004', '古罗马史', 'Edward Gibbon', 'History', 6, 'Ancient Rome', 'Europe', 'Roman Emperors'),
('H005', '剑桥中国史', '费正清', 'History', 10, 'All Periods', 'China', 'Multiple');

-- 插入哲学类图书 (P001-P005)
INSERT INTO books (isbn, title, author, type, stock, philosophical_school, key_concepts, thinkers) VALUES
('P001', '苏格拉底之死', '柏拉图', 'Philosophy', 6, 'Ancient Greek', 'Ethics, Justice', 'Socrates, Plato'),
('P002', '纯粹理性批判', '康德', 'Philosophy', 4, 'German Idealism', 'Epistemology', 'Immanuel Kant'),
('P003', '存在与时间', '海德格尔', 'Philosophy', 5, 'Phenomenology', 'Being, Time', 'Martin Heidegger'),
('P004', '道德形而上学', '康德', 'Philosophy', 7, 'Ethics', 'Morality', 'Immanuel Kant'),
('P005', '查拉图斯特拉如是说', '尼采', 'Philosophy', 8, 'Existentialism', 'Will to Power', 'Friedrich Nietzsche');

-- 插入经济类图书 (E001-E005)
INSERT INTO books (isbn, title, author, type, stock, economic_school, market_type, application_field) VALUES
('E001', '国富论', '亚当·斯密', 'Economics', 10, 'Classical', 'Free Market', 'Political Economy'),
('E002', '资本论', '卡尔·马克思', 'Economics', 7, 'Marxian', 'Socialist', 'Political Economy'),
('E003', '经济学原理', '曼昆', 'Economics', 15, 'Modern', 'Mixed', 'General Economics'),
('E004', '就业、利息和货币通论', '凯恩斯', 'Economics', 8, 'Keynesian', 'Mixed', 'Macroeconomics'),
('E005', '微观经济学', 'Robert Pindyck', 'Economics', 12, 'Neoclassical', 'Market', 'Microeconomics');

-- 插入医学类图书 (M001-M005)
INSERT INTO books (isbn, title, author, type, stock, medical_specialty, clinical_focus, practice_area) VALUES
('M001', '内科学', '葛卫平', 'Medicine', 12, 'Internal Medicine', 'General', 'Clinical'),
('M002', '外科学', '赵玉沛', 'Medicine', 8, 'Surgery', 'General Surgery', 'Clinical'),
('M003', '神经科��', 'Eric Kandel', 'Medicine', 6, 'Neurology', 'Brain', 'Research'),
('M004', '病理学', 'Robbins', 'Medicine', 10, 'Pathology', 'General', 'Diagnostic'),
('M005', '药理学', 'Katzung', 'Medicine', 9, 'Pharmacology', 'Drugs', 'Treatment');

-- 插入教育类图书 (D001-D005)
INSERT INTO books (isbn, title, author, type, stock, education_level, subject, teaching_method) VALUES
('D001', '教育学原理', '王道俊', 'Education', 10, 'University', 'Pedagogy', 'Theoretical'),
('D002', '课程与教学论', '钟启泉', 'Education', 8, 'All Levels', 'Curriculum', 'Practical'),
('D003', '教育心理学', '张大均', 'Education', 12, 'All Levels', 'Psychology', 'Applied'),
('D004', '教育社会学', '李其龙', 'Education', 7, 'Higher Ed', 'Sociology', 'Research'),
('D005', '比较教育学', '顾明远', 'Education', 6, 'All Levels', 'Comparative', 'Research');

-- 插入法律类图书 (W001-W005)
INSERT INTO books (isbn, title, author, type, stock, legal_system, jurisdiction, legal_field) VALUES
('W001', '法理学', '张文显', 'Law', 8, 'Civil Law', 'China', 'Jurisprudence'),
('W002', '刑法学', '高铭暄', 'Law', 10, 'Criminal Law', 'China', 'Criminal'),
('W003', '民法总论', '王泽鉴', 'Law', 6, 'Civil Law', 'China', 'Civil'),
('W004', '国际法', '梁西', 'Law', 7, 'International Law', 'International', 'Public'),
('W005', '商法原理', '范健', 'Law', 9, 'Commercial Law', 'China', 'Business');

-- 插入备份记录
INSERT INTO backup_records (backup_time, file_name, status, record_count) VALUES
(DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 1 DAY), 'books_backup_20240301_120000.json', 'SUCCESS', 10),
(DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 2 DAY), 'books_backup_20240302_120000.json', 'SUCCESS', 10),
(DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 3 DAY), 'books_backup_20240303_120000.json', 'SUCCESS', 10); 