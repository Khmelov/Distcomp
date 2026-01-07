# Database Setup Instructions

## Creating the distcomp Database

The application requires a PostgreSQL database named `distcomp`. Follow these steps to create it:

### Method 1: Using psql Command Line

Open PowerShell or Command Prompt and run:

```bash
psql -U postgres -c "CREATE DATABASE distcomp;"
```

When prompted, enter the password: `1902`

### Method 2: Using psql Interactively

1. Open PowerShell or Command Prompt
2. Run: `psql -U postgres`
3. Enter password when prompted: `1902`
4. Execute: `CREATE DATABASE distcomp;`
5. Exit: `\q`

### Method 3: Using pgAdmin

1. Open pgAdmin
2. Connect to your PostgreSQL server
3. Right-click on "Databases" → "Create" → "Database"
4. Enter database name: `distcomp`
5. Click "Save"

## After Creating the Database

1. Start the application
2. Liquibase will automatically:
   - Create the `distcomp` schema in the `distcomp` database
   - Create all tables with `tbl_` prefix:
     - `tbl_user`
     - `tbl_label`
     - `tbl_article`
     - `tbl_post`
     - `tbl_article_label`

## Database Connection Details

- **Host**: localhost
- **Port**: 5432
- **Database**: distcomp
- **Schema**: distcomp
- **Username**: postgres
- **Password**: 1902

## Troubleshooting

If you get authentication errors:
- Verify that the `postgres` user password is `1902`
- Check that PostgreSQL is running
- Ensure the `postgres` user has permissions to create databases

