from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('writers', '0005_rename_writer_id_writer_id'),
    ]

    operations = [
        migrations.AddField(
            model_name='writer',
            name='role',
            field=models.CharField(
                choices=[('ADMIN', 'Admin'), ('CUSTOMER', 'Customer')],
                default='CUSTOMER',
                max_length=16,
            ),
        ),
    ]
