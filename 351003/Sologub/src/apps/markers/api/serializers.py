from rest_framework import serializers
from apps.markers.models import Marker


class MarkerSerializer(serializers.ModelSerializer):

    class Meta:
        model = Marker
        fields = '__all__'
