import subprocess
import sys
import os
import time

BASE_DIR = os.path.dirname(os.path.abspath(__file__))

print("=" * 50)
print("Запуск t340 (Publisher + Discussion + Kafka)")
print("=" * 50)

# Запуск Discussion
disc_dir = os.path.join(BASE_DIR, 'discussion')
if os.path.exists(disc_dir):
    print("[Discussion] Запуск...")
    disc = subprocess.Popen(
        [sys.executable, 'run_discussion.py'],
        cwd=disc_dir,
        creationflags=subprocess.CREATE_NEW_CONSOLE
    )
    time.sleep(2)
else:
    print("[Discussion] Папка не найдена!")

# Запуск Publisher
pub_dir = os.path.join(BASE_DIR, 'publisher')
if os.path.exists(pub_dir):
    print("[Publisher] Запуск...")
    pub = subprocess.Popen(
        [sys.executable, 'run_publisher.py'],
        cwd=pub_dir,
        creationflags=subprocess.CREATE_NEW_CONSOLE
    )
else:
    print("[Publisher] Папка не найдена!")

print("=" * 50)
print("Publisher:  http://localhost:24110")
print("Discussion: http://localhost:24130")
print("=" * 50)
print("Закройте окна сервисов для остановки.")
input("Нажмите Enter для выхода...")