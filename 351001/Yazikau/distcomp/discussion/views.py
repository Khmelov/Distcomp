from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status
from api.models import CassandraComment, Tweet # Добавил Tweet
from cassandra.cqlengine import connection
import os
import time

DB_INITIALIZED = False

def ensure_db():
    global DB_INITIALIZED
    if DB_INITIALIZED: return
    try:
        host = os.environ.get('CASSANDRA_HOST', 'cassandra')
        connection.setup([host], "distcomp", protocol_version=3)
        DB_INITIALIZED = True
    except Exception as e:
        print(f"Ошибка БД Cassandra: {e}")

class CommentListCreateView(APIView):
    def get(self, request):
        ensure_db()
        comments = CassandraComment.objects.all().limit(100)
        data = []
        for c in comments:
            try:
                data.append({"id": int(c.id), "tweetId": int(c.tweetId), "content": c.content})
            except:
                continue
        return Response(data)

    def post(self, request):
        ensure_db()
        data = request.data.copy()

        # --- КРИТИЧЕСКИЕ ПРАВКИ ВАЛИДАЦИИ ---
        if 'tweetId' not in data or 'content' not in data:
            return Response(status=400)
        if len(str(data.get('content', ''))) < 4:
            return Response(status=400)
        try:
            if not Tweet.objects.filter(id=int(data['tweetId'])).exists():
                return Response(status=404)
        except:
            return Response(status=400)
        # ------------------------------------

        if 'country' not in data:
            data['country'] = 'Default'

        data['id'] = int(data.get('id', time.time() * 1000))

        try:
            comment = CassandraComment.create(**data)
            return Response({"id": int(comment.id), "tweetId": int(comment.tweetId), "content": comment.content}, status=201)
        except Exception as e:
            return Response({"detail": str(e)}, status=400)

class CommentDetailView(APIView):
    def get(self, request, pk):
        ensure_db()
        try:
            # ДЛЯ DISCUSSION: ИГНОРИРУЕМ REDIS, ИДЕМ В КАССАНДРУ
            comment = CassandraComment.objects.filter(id=int(pk)).allow_filtering().first()
            if not comment:
                return Response({"detail": "Not found"}, status=404)
            return Response({"id": int(comment.id), "tweetId": int(comment.tweetId), "content": comment.content})
        except:
            return Response({"detail": "Not found"}, status=404)

    def put(self, request, pk):
        ensure_db()
        try:
            comment = CassandraComment.objects.filter(id=int(pk)).allow_filtering().first()
            if not comment: return Response(status=404)
            if 'content' in request.data: comment.content = request.data['content']
            if 'tweetId' in request.data: comment.tweetId = int(request.data['tweetId'])
            comment.save()
            return Response({"id": int(comment.id), "tweetId": int(comment.tweetId), "content": comment.content})
        except:
            return Response(status=400)

    def delete(self, request, pk):
        ensure_db()
        try:
            comment = CassandraComment.objects.filter(id=int(pk)).allow_filtering().first()
            if not comment:
                return Response(status=404) # Фикс для теста
            comment.delete()
            return Response(status=204)
        except:
            return Response(status=400)