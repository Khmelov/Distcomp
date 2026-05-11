from rest_framework import serializers

from apps.writers.models import Writer


class LoginRequestSerializer(serializers.Serializer):
    login = serializers.CharField(max_length=64)
    password = serializers.CharField(max_length=128, trim_whitespace=False, write_only=True)


class RegisterSerializer(serializers.ModelSerializer):
    """v2.0 registration payload.

    ``role`` defaults to CUSTOMER when the caller is anonymous (i.e. during
    registration). An ADMIN token is required to create another ADMIN; this
    is enforced in the view.
    """

    role = serializers.ChoiceField(choices=Writer.ROLE_CHOICES, required=False,
                                   default=Writer.ROLE_CUSTOMER)

    class Meta:
        model = Writer
        fields = ("id", "login", "password", "firstname", "lastname", "role")
        extra_kwargs = {"password": {"write_only": True, "min_length": 8}}

    def validate_login(self, value):
        if Writer.objects.filter(login=value).exists():
            raise serializers.ValidationError("Writer with this login already exists")
        return value


class WriterSecureSerializer(serializers.ModelSerializer):
    """Serializer used by the v2.0 writer CRUD – never returns the password."""

    class Meta:
        model = Writer
        fields = ("id", "login", "firstname", "lastname", "role")
        read_only_fields = ("id",)

    def validate_login(self, value):
        qs = Writer.objects.filter(login=value)
        if self.instance is not None:
            qs = qs.exclude(pk=self.instance.pk)
        if qs.exists():
            raise serializers.ValidationError("Writer with this login already exists")
        return value
