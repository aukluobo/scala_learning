class Queue[T](
	private val leading:List[T],
	private val tailing:List[T]
){
	private def mirror = 
		if(leading.isEmpty) 
			new Queue(tailing.reverse,Nil)
		else
			this
	def head=mirror.leading.head
	def tail={
		val q = mirror
		new Queue(q.leading.tail,q.tailing)
	}
	def enqueue(x:T)={
		new Queue(leading, x :: tailing)
	}
}