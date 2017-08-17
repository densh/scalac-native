package benchmarks

class DummyBenchmark extends Benchmark[Int] {

  override val runningTime: BenchmarkRunningTime = ShortRunningTime

  override def run(): Int =
    (1 to 1000).sum

  override def check(t: Int): Boolean =
    t == 500500
}