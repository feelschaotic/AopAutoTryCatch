# AopAutoTryCatch

Use Javassist, a custom Gradle plug to implement automatic try-catch
AOP思想取代防范性try-catch

## 技术点
- Javassist
- 自定义Gradle插件
- Transform

## 使用方法
```
  /**
     * 无崩溃，会catch所有Exception
     */
    @AutoTryCatch
    public void catchAllException() {
        int i = 1 / 0;
    }

    /**
     * 此处崩溃，因为只会catch指定的NullPointerException
     */
    @AutoTryCatch(NullPointerException.class)
    public void catchNullPointerException() {
       int i = 1 / 0;
    }

    /**
     * 无崩溃
     */
    @AutoTryCatch({NullPointerException.class, ArithmeticException.class})
    public void catchNullPointerAndClassCastException() {
        List arrays = null;
        int size = arrays.size();
        size = 1 / 0;
    }
```
> 详细介绍：[一文应用 AOP | 最全选型考量 + 边剖析经典开源库边实践，美滋滋](https://www.jianshu.com/p/42ce95450adb)
