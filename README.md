# Ant Colony Optimization in Scala [![Build Status](https://travis-ci.org/nwtgck/ant-colony-optimization-scala.svg?branch=master)](https://travis-ci.org/nwtgck/ant-colony-optimization-scala)

## How to run Main

```
cd <this repo>
sbt "runMain Main"
```
### Options

You can specify some options bellow

```bash
sbt "runMain Main --help"
```

```
Usage: TSP solver by Ant Colony Optimization [options] [<path of .tsp>]

  --help                 prints this usage text
  -i, --n-iters <value>  The number of iteration
  -a, --n-ants <value>   The number of ants
  --alpha <value>        alpha
  --beta <value>         beta
  --q <value>            Q
  --ro <value>           ro
  --outpath <value>      output directory path
  --seed <value>         random seed
  <path of .tsp> 
```

### An example

```bash
sbt "runMain Main --n-ants=10 --seed=12 --n-iters=5 ./tsp/wi29.tsp"
```


## How to run LegacyMain

```
cd <this repo>
sbt "runMain LegacyMain"
```

## Reference

* https://qiita.com/EnsekiTT/items/9b13ceba391221687f42
