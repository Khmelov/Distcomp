from rest_framework import viewsets, status
from rest_framework.response import Response
from rest_framework.views import APIView
from .models import Author, Mark, Tweet, CassandraComment
from .serializers import AuthorSerializer, MarkSerializer, TweetSerializer
from cassandra.cqlengine import connection
from cassandra.cqlengine.management import sync_table
import os
import time
import redis
import json

REDIS_HOST = os.environ.get('REDIS_HOST', 'redis')
SERVICE_NAME = os.environ.get('SERVICE_NAME', 'UNKNOWN')

cache = redis.StrictRedis(
    host=REDIS_HOST,
    port=6379,
    db=0,
    decode_responses=True,
    socket_timeout=1
)

class AuthorViewSet(viewsets.ModelViewSet):
    queryset = Author.objects.all()
    serializer_class = AuthorSerializer

    def create(self, request, *args, **kwargs):
        if Author.objects.filter(login=request.data.get('login')).exists():
            return Response({"detail": "exists"}, status=status.HTTP_403_FORBIDDEN)
        return super().create(request, *args, **kwargs)

class MarkViewSet(viewsets.ModelViewSet):
    queryset = Mark.objects.all()
    serializer_class = MarkSerializer

class TweetViewSet(viewsets.ModelViewSet):
    queryset = Tweet.objects.all()
    serializer_class = TweetSerializer

    def create(self, request, *args, **kwargs):
        if Tweet.objects.filter(title=request.data.get('title')).exists():
            return Response({"detail": "exists"}, status=status.HTTP_403_FORBIDDEN)
        return super().create(request, *args, **kwargs)

DB_INITIALIZED = False

def ensure_db():
    global DB_INITIALIZED
    if DB_INITIALIZED: return
    try:
        host = os.environ.get('CASSANDRA_HOST', 'cassandra')
        from cassandra.cluster import Cluster
        cluster = Cluster([host], port=9042)
        session = cluster.connect()
        session.execute("""
        CREATE KEYSPACE IF NOT EXISTS distcomp
        WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'}
        """)
        cluster.shutdown()
        # Принудительно устанавливаем дефолтный keyspace здесь!
        connection.setup([host], "distcomp", protocol_version=3)
        sync_table(CassandraComment)
        DB_INITIALIZED = True
    except Exception as e:
        print(f"Ожидание Cassandra: {e}")
        # Если база не ответила, принудительно настраиваем коннект без проверки,
        # чтобы библиотека cqlengine не падала с ошибкой "keyspace not set"
        try:
            host = os.environ.get('CASSANDRA_HOST', 'cassandra')
            connection.setup([host], "distcomp", protocol_version=3)
        except:
            pass

class CommentListCreateView(APIView):
    def get(self, request):
        ensure_db()
        try:
            comments = CassandraComment.objects.all().limit(100)
            data = []
            for c in comments:
                try:
                    data.append({"id": int(c.id), "tweetId": int(c.tweetId), "content": c.content})
                except (ValueError, TypeError):
                    continue
            return Response(data, status=status.HTTP_200_OK)
        except Exception as e:
            return Response([], status=status.HTTP_200_OK)

    def post(self, request):
        ensure_db()
        data = request.data.copy()

        if 'tweetId' not in data or 'content' not in data:
            return Response({"detail": "Required fields missing"}, status=400)

        if len(str(data.get('content', ''))) < 4:
            return Response({"detail": "Content too short"}, status=400)

        try:
            if not Tweet.objects.filter(id=int(data['tweetId'])).exists():
                return Response({"detail": "Tweet not found"}, status=404)
        except:
            return Response({"detail": "Invalid tweetId"}, status=400)

        if 'country' not in data:
            data['country'] = 'Default'

        data['id'] = int(data.get('id', time.time() * 1000))

        try:
            comment = CassandraComment.create(**data)
            response_data = {"id": int(comment.id), "tweetId": int(comment.tweetId), "content": comment.content}

            if SERVICE_NAME == 'PUBLISHER':
                try:
                    cache.set(str(comment.id), json.dumps(response_data))
                except:
                    pass

            return Response(response_data, status=201)
        except Exception as e:
            return Response({"detail": str(e)}, status=400)

class CommentDetailView(APIView):
    def get(self, request, pk):
        pk_str = str(pk)

        if SERVICE_NAME != 'DISCUSSION':
            try:
                cached_val = cache.get(pk_str)
                if cached_val:
                    try:
                        data = json.loads(cached_val)
                        if isinstance(data, dict):
                            return Response(data, status=200)
                    except:
                        pass
            except:
                pass

        ensure_db()
        try:
            comment = CassandraComment.objects.filter(id=int(pk)).allow_filtering().first()
            if not comment:
                return Response({"detail": "Not found"}, status=status.HTTP_404_NOT_FOUND)

            db_data = {"id": int(comment.id), "tweetId": int(comment.tweetId), "content": comment.content}

            if SERVICE_NAME != 'DISCUSSION':
                try:
                    cache.set(pk_str, json.dumps(db_data))
                except:
                    pass

            return Response(db_data, status=status.HTTP_200_OK)
        except Exception as e:
            return Response({"detail": "Error/Not found"}, status=status.HTTP_404_NOT_FOUND)

    def put(self, request, pk):
        ensure_db()
        try:
            comment = CassandraComment.objects.filter(id=int(pk)).allow_filtering().first()
            if not comment: return Response(status=404)

            if 'content' in request.data: comment.content = request.data['content']
            if 'tweetId' in request.data: comment.tweetId = int(request.data['tweetId'])
            comment.save()

            data = {"id": int(comment.id), "tweetId": int(comment.tweetId), "content": comment.content}

            if SERVICE_NAME == 'PUBLISHER':
                try:
                    cache.set(str(pk), json.dumps(data))
                except:
                    pass

            return Response(data)
        except:
            return Response(status=400)

    def delete(self, request, pk):
        ensure_db()
        try:
            comment = CassandraComment.objects.filter(id=int(pk)).allow_filtering().first()
            if not comment:
                return Response({"detail": "Not found"}, status=404)

            comment.delete()
            try:
                cache.delete(str(pk))
            except:
                pass
            return Response(status=204)
        except:
            return Response(status=400)