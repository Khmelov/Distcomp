using lab_1.Domain;
using Microsoft.AspNetCore.Authentication.OAuth.Claims;
using System.Diagnostics.Eventing.Reader;

namespace lab_1.Repositories
{
    public class Repository<T> // Общий класс репозитория для хранения объектов типа T в памяти
    {
        private Dictionary<long?, T> _Dict; // Словарь для хранения данных: ключ - ID, значение - объект

        public long NextId { get => _Dict.Count; } // Следующий ID для нового объекта (количество элементов)
        public Repository() { _Dict = new(); } // Конструктор: инициализация пустого словаря
        
        // Добавить новый объект в репозиторий с автоматически сгенерированным ID
        public void AddValue(T value)
        {
            _Dict.Add(NextId, value); // Добавить в словарь
        }

        // Удалить объект по ID
        public bool DeleteValue(long id)
        {
            return _Dict.Remove(id); // Вернуть true, если удалено
        }

        // Найти объект по ID
        public T FindById(long? id)
        {
            T res;
            try
            {
                res = _Dict[id]; // Получить из словаря
            }
            catch (KeyNotFoundException)
            {
                Console.Error.WriteLine("Could not find"); // Лог ошибки, если не найден
                res = default(T); // Вернуть null или default
            }
            return res;
        }

        // Обновить объект по ID
        public void UpdateValue(T entity, long? id)
        {
            _Dict[id] = entity; // Заменить значение в словаре
        }

        // Проверить, существует ли объект с данным ID
        public bool Contains(long? id) => id != null && _Dict.ContainsKey(id);

        // Получить все объекты (метод назван GetAuthors, но работает для любого T)
        public IEnumerable<T> GetAuthors() => _Dict.Values;

    }
}
