import os
import requests
from rest_framework import viewsets, status
from rest_framework.response import Response
from rest_framework.request import Request


def _discussion_url() -> str:
    base = os.environ.get("DISCUSSION_SERVICE_URL", "http://localhost:24130/api/v1.0")
    return base.rstrip("/")


def _proxy(request: Request, path_suffix: str = "", method: str = None) -> Response:
    method = (method or request.method).upper()
    url = f"{_discussion_url()}/notes{path_suffix}"
    params = request.query_params
    headers = {"Content-Type": "application/json"}
    try:
        if method == "GET":
            r = requests.get(url, params=params, timeout=10)
        elif method == "POST":
            r = requests.post(url, json=request.data, params=params, timeout=10)
        elif method == "PUT":
            r = requests.put(url, json=request.data, params=params, timeout=10)
        elif method == "PATCH":
            r = requests.patch(url, json=request.data, params=params, timeout=10)
        elif method == "DELETE":
            r = requests.delete(url, params=params, timeout=10)
        else:
            return Response(status=status.HTTP_405_METHOD_NOT_ALLOWED)
    except requests.RequestException as e:
        return Response(
            {"detail": f"Discussion service error: {str(e)}"},
            status=status.HTTP_502_BAD_GATEWAY,
        )
    try:
        data = r.json() if r.content else None
    except Exception:
        data = {"detail": r.text or f"Discussion returned {r.status_code}"} if r.content else None
    return Response(data=data, status=r.status_code)


class NoteViewSet(viewsets.ViewSet):
    """
    Прокси к микросервису discussion (Note хранятся в Cassandra).
    Внешнее API приложения остаётся прежним: /api/v1.0/notes.
    """

    def list(self, request: Request):
        return _proxy(request)

    def create(self, request: Request):
        return _proxy(request)

    def retrieve(self, request: Request, pk=None):
        return _proxy(request, path_suffix=f"/{pk}")

    def update(self, request: Request, pk=None):
        return _proxy(request, path_suffix=f"/{pk}")

    def partial_update(self, request: Request, pk=None):
        return _proxy(request, path_suffix=f"/{pk}", method="PUT")

    def destroy(self, request: Request, pk=None):
        return _proxy(request, path_suffix=f"/{pk}")
