Тествое задания, приложения по workout

![image](https://github.com/user-attachments/assets/44da0179-87dd-453b-b2bd-722ff2e7ff2c)
![image](https://github.com/user-attachments/assets/dcd41c79-b10c-4d11-9193-1d7c0322280e)
![image](https://github.com/user-attachments/assets/7fdb6cf2-36de-4b7a-b7db-69be673ad4d6)
![image](https://github.com/user-attachments/assets/b4c89c2e-a95e-4982-80bc-890cdc79c87d)
![image](https://github.com/user-attachments/assets/b3f43b66-1687-42e7-ac19-73a8957bef68)
![image](https://github.com/user-attachments/assets/205bdb3a-fd13-4162-a214-9ae22ee15680)



Использовались библиотеки:
-ExoPLayer
-Retrofit
-Jetpack Navigation
-Views/Fragments integration
-kps + Hilt and Dagger
-Coroutine

Hilt and Dagger - использовался для красоты и удобства. Можно было сделать без использования данной библиотеки. 
Достаточно было использовать object.
object используется для предоставления экземпляра ExoPlayer, чтобы не создавать и удалять данный объект каждый раз, 
когда создается или удаляется фрагмент с видеоплеером. ExoPlayer инициализируется в WorkoutApplication
Так же хотел кэширование данных, через DataStore/SharedPreference, но не хватило времени.
Используется архитектура MVI.
Имеется одна активити, 2 фрагмента:
1.WorkoutlistedFragment - отображает список тренеровок, позволяет искать тренировки по поиску или использовать фильтрацию
2.WokroutFragment - предоставляет подробное описание тренеровки, с демонстрацией видое. Имеется функционал: изменения скорость видео,
изменения качества видео, открытие видео на весь экран с последующим переворотом экрана.
Не был реализован: 
  1.после окончания видео, начинать с начала с пустя 10 секунд. (реализовать позже)
  2.передача переменной из фрагмента во viewModel через фабрику фракментов. (реализовать позже)

  Имеется анимация перехода из одного фрагмента в другой.

  Используется List, вместо array, так как нет строгой необходимости, + мы не знаем точный размер, для выделения памяти.

  Имеется отмена coroutine в WokroutFragment, когда выполняется запрос на сервер, а пользователь покидает данный фрагмент, так же отмена есть при фильтрации списка
