from rest_framework import mixins, viewsets
from apps.notes.api.serializers import NoteSerializer
from apps.notes.models import Note


class NoteViewSet(viewsets.GenericViewSet,
                    mixins.CreateModelMixin,
                    mixins.ListModelMixin,
                    mixins.RetrieveModelMixin,
                    mixins.UpdateModelMixin,
                    mixins.DestroyModelMixin):

    def get_queryset(self):
        return Note.objects.all()

    def get_serializer_class(self):
        return NoteSerializer
