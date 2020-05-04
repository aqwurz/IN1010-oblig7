import java.util.Iterator;

class Lenkeliste<T> implements Liste<T>{
	
	protected class Node{
		private Node neste;
		private Node forrige;
		T data;
		public Node(T x, Node fr, Node nst) {
			data = x;
			forrige = fr;
			neste = nst;
		}
		public String toString() {
			return "Data: " + data;
		}
		public void settNeste(Node nst) {
			neste = nst;
		}
		public void settForrige(Node fr) {
			forrige = fr;
		}
		public Node faaNeste() {
			return neste;
		}
		public Node faaForrige() {
			return forrige;
		}
		public Node finnNode(int avstand, int indeks) {
			if (avstand == 0) {
				return this;
			}
			else {
				
				if (this == slutt) {
					if (avstand > 0) //Om noden vi skal finne er siste node
						throw new UgyldigListeIndeks(indeks);
				}
				return faaNeste().finnNode(avstand - 1, indeks);
			}
		}
		
		public T faaData() {
			return data;
		}
	}
	
	protected Node start;
	protected Node slutt;
	protected int stoerrelse;
	
	class LenkelisteIterator implements Iterator<T> {
		private int pos = 0;

		public boolean hasNext() {
			return pos < stoerrelse;
		}

		public T next() {
			return hent(pos++);
		}

		//fjerner den noden du er på
		public void remove() {
			fjern(pos);
			return ;
		}
	}
	
	public Lenkeliste() {
		start = new Node(null, null, null); //Her lages to tomme noder for å unngå mye koding med tanke på edge-cases
		slutt = new Node(null, start, null);
		start.settNeste(slutt);
		stoerrelse = 0;
	}
	public Node finnNode(int avstand) {
		if (avstand < 0)
			throw new UgyldigListeIndeks(avstand);
		return  start.faaNeste().finnNode(avstand, avstand);
	}
	
	public void leggTil(T x) {
		Node forrige = slutt.faaForrige();
		Node ny =  new Node(x, forrige, slutt);
		slutt.settForrige(ny);
		forrige.settNeste(ny);
		stoerrelse++;
	}
	public void leggTil(int pos, T x) {
		Node neste = finnNode(pos);
		Node forrige = neste.faaForrige();
		Node nyNode = new Node(x, forrige, neste);
		forrige.settNeste(nyNode);
		neste.settForrige(nyNode);
		stoerrelse++;
	}
	public void sett(int pos, T x) {
		Node overskrives = finnNode(pos);
		if (overskrives == null || overskrives == start || overskrives == slutt)
			throw new UgyldigListeIndeks(pos);
		Node neste = overskrives.faaNeste();
		Node forrige = overskrives.faaForrige();
		Node nyNode = new Node(x, forrige, neste);
		forrige.settNeste(nyNode);
		neste.settForrige(nyNode);
	}
	public T fjern() {
		Node slettet = start.faaNeste();
		if (slettet == slutt || slettet == null)
			throw new UgyldigListeIndeks(-1);
		slettet.faaNeste().settForrige(start);
		start.settNeste(slettet.faaNeste());
		stoerrelse--;
		return slettet.faaData();
	}
	public T fjern(int pos) {
		Node slettes = finnNode(pos);
		if (slettes == null || slettes == start || slettes == slutt)
			throw new UgyldigListeIndeks(pos);
		Node neste = slettes.faaNeste();
		Node forrige = slettes.faaForrige();
		neste.settForrige(forrige);
		forrige.settNeste(neste);
		stoerrelse--;
		return slettes.faaData();
	}
	
	public int stoerrelse() {
		return stoerrelse;
	}
	public T hent(int pos) {
		Node node = finnNode(pos);
		if (node == null || node == start || node == slutt)
			throw new UgyldigListeIndeks(pos);
		return finnNode(pos).faaData();
	}
	
	public Iterator<T> iterator() {
		return new LenkelisteIterator();
	}

	public String toString() {
		String str = "";
		for (T t : this) {
			str += t.toString()+"\n";
		}
		return str;
	}
}
