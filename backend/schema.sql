
-- Create the Database
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'ContactMgmtDB')
BEGIN
    CREATE DATABASE ContactMgmtDB;
END
GO

USE ContactMgmtDB;
GO

-- 1. AppUsers Table
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[AppUsers]') AND type in (N'U'))
BEGIN
    CREATE TABLE AppUsers (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        email NVARCHAR(255) UNIQUE NULL,
        phone NVARCHAR(50) UNIQUE NULL,
        password NVARCHAR(255) NOT NULL,
        roles NVARCHAR(255) DEFAULT 'ROLE_USER',
        created_at DATETIME DEFAULT GETDATE()
    );
END
GO

-- 2. Contacts Table
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Contacts]') AND type in (N'U'))
BEGIN
    CREATE TABLE Contacts (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        user_id BIGINT NOT NULL,
        first_name NVARCHAR(100) NOT NULL,
        last_name NVARCHAR(100) NOT NULL,
        title NVARCHAR(50) NOT NULL, -- Mr., Ms., Dr., etc.
        created_at DATETIME DEFAULT GETDATE(),
        CONSTRAINT FK_Contacts_AppUsers FOREIGN KEY (user_id) REFERENCES AppUsers(id) ON DELETE CASCADE
    );
END
GO

-- 3. ContactEmails Table
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[ContactEmails]') AND type in (N'U'))
BEGIN
    CREATE TABLE ContactEmails (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        contact_id BIGINT NOT NULL,
        email_address NVARCHAR(255) NOT NULL,
        label NVARCHAR(50) NOT NULL, -- e.g., 'Work', 'Personal'
        CONSTRAINT FK_ContactEmails_Contacts FOREIGN KEY (contact_id) REFERENCES Contacts(id) ON DELETE CASCADE
    );
END
GO

-- 4. ContactPhones Table
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[ContactPhones]') AND type in (N'U'))
BEGIN
    CREATE TABLE ContactPhones (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        contact_id BIGINT NOT NULL,
        phone_number NVARCHAR(50) NOT NULL,
        label NVARCHAR(50) NOT NULL, -- e.g., 'Work', 'Home', 'Personal'
        CONSTRAINT FK_ContactPhones_Contacts FOREIGN KEY (contact_id) REFERENCES Contacts(id) ON DELETE CASCADE
    );
END
GO

-- Create index for search optimization
CREATE INDEX IX_Contacts_Names ON Contacts(first_name, last_name);
GO
