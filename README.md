# AopAutoTryCatch

Use Javassist, a custom Gradle plug to implement automatic try-catch

用 Javassist 实现 AOP 思想，达到一个注解即可自动加 try-catch 的目的，当然，AOP 的应用场景还有很多很多，这里抛砖引玉，希望给大家一些参考。

## 一、通过本项目你能学到什么技术点？

- Javassist
- 自定义Gradle插件
- Transform

## 二、如何使用

使用注解 `@AutoTryCatch`来捕获异常

1. 支持 catch 所有异常
```
  /**
     * 无崩溃，会catch所有Exception
     */
    @AutoTryCatch
    public void catchAllException() {
        int i = 1 / 0;
    }
```

2. 支持 catch 指定异常

```
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
> 详细介绍见我的博文：[一文应用 AOP | 最全选型考量 + 边剖析经典开源库边实践，美滋滋](https://www.jianshu.com/p/42ce95450adb)
