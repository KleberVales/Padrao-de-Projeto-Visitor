package principal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

enum Color {
	RED, GREEN
}

abstract class Tree {
	private int value;
	private Color color;
	private int depth;

	public Tree(int value, Color color, int depth) {
		this.value = value;
		this.color = color;
		this.depth = depth;
	}

	public int getValue() {
		return value;
	}

	public Color getColor() {
		return color;
	}

	public int getDepth() {
		return depth;
	}

	public abstract void accept(TreeVis visitor);
}

class TreeNode extends Tree {
	private List<Tree> children = new ArrayList<>();

	public TreeNode(int value, Color color, int depth) {
		super(value, color, depth);
	}

	public void addChild(Tree child) {
		children.add(child);
	}

	public List<Tree> getChildren() {
		return children;
	}

	@Override
	public void accept(TreeVis visitor) {
		visitor.visitNode(this);
		for (Tree child : children) {
			child.accept(visitor);
		}
	}
}

class TreeLeaf extends Tree {
	public TreeLeaf(int value, Color color, int depth) {
		super(value, color, depth);
	}

	@Override
	public void accept(TreeVis visitor) {
		visitor.visitLeaf(this);
	}
}

abstract class TreeVis {
	public abstract int getResult();

	public abstract void visitNode(TreeNode node);

	public abstract void visitLeaf(TreeLeaf leaf);
}

class SumInLeavesVisitor extends TreeVis {
	private int sum = 0;

	@Override
	public int getResult() {
		return sum;
	}

	@Override
	public void visitNode(TreeNode node) {
		// Não faz nada para nós internos
	}

	@Override
	public void visitLeaf(TreeLeaf leaf) {
		sum += leaf.getValue();
	}
}

class ProductRedNodesVisitor extends TreeVis {
	private long product = 1;
	private final int MODULO = 1_000_000_007;

	@Override
	public int getResult() {
		return (int) product;
	}

	@Override
	public void visitNode(TreeNode node) {
		if (node.getColor() == Color.RED) {
			product = (product * node.getValue()) % MODULO;
		}
	}

	@Override
	public void visitLeaf(TreeLeaf leaf) {
		if (leaf.getColor() == Color.RED) {
			product = (product * leaf.getValue()) % MODULO;
		}
	}
}

class FancyVisitor extends TreeVis {
	private int sumEvenDepthNonLeaf = 0;
	private int sumGreenLeaf = 0;

	@Override
	public int getResult() {
		return Math.abs(sumEvenDepthNonLeaf - sumGreenLeaf);
	}

	@Override
	public void visitNode(TreeNode node) {
		if (node.getDepth() % 2 == 0) {
			sumEvenDepthNonLeaf += node.getValue();
		}
	}

	@Override
	public void visitLeaf(TreeLeaf leaf) {
		if (leaf.getColor() == Color.GREEN) {
			sumGreenLeaf += leaf.getValue();
		}
	}
}

class Resolucao {
	
	public static Tree solve() {
		Scanner scanner = new Scanner(System.in);
		int n = scanner.nextInt();

		int[] values = new int[n];
		for (int i = 0; i < n; i++) {
			values[i] = scanner.nextInt();
		}

		Color[] colors = new Color[n];
		for (int i = 0; i < n; i++) {
			colors[i] = scanner.nextInt() == 0 ? Color.RED : Color.GREEN;
		}

		Map<Integer, List<Integer>> edges = new HashMap<>();
		for (int i = 0; i < n - 1; i++) {
			int u = scanner.nextInt();
			int v = scanner.nextInt();
			edges.computeIfAbsent(u, k -> new ArrayList<>()).add(v);
			edges.computeIfAbsent(v, k -> new ArrayList<>()).add(u);
		}

		scanner.close();

		return buildTree(1, 0, values, colors, edges, new HashSet<>());
	}

	private static Tree buildTree(int node, int depth, int[] values, Color[] colors, Map<Integer, List<Integer>> edges,
			Set<Integer> visited) {
		visited.add(node);
		List<Integer> children = edges.get(node);
		boolean isLeaf = true;

		TreeNode treeNode = new TreeNode(values[node - 1], colors[node - 1], depth);
		if (children != null) {
			for (int child : children) {
				if (!visited.contains(child)) {
					isLeaf = false;
					treeNode.addChild(buildTree(child, depth + 1, values, colors, edges, visited));
				}
			}
		}

		if (isLeaf) {
			return new TreeLeaf(values[node - 1], colors[node - 1], depth);
		} else {
			return treeNode;
		}
	}
}
public class Main {
    public static void main(String[] args) {
        // Constrói a árvore a partir da entrada do usuário
        Tree root = Resolucao.solve();

        // Cria os visitantes
        SumInLeavesVisitor sumVisitor = new SumInLeavesVisitor();
        ProductRedNodesVisitor productVisitor = new ProductRedNodesVisitor();
        FancyVisitor fancyVisitor = new FancyVisitor();

        // Executa a visita na árvore
        root.accept(sumVisitor);
        root.accept(productVisitor);
        root.accept(fancyVisitor);

        // Imprime os resultados
        System.out.println(sumVisitor.getResult());
        System.out.println(productVisitor.getResult());
        System.out.println(fancyVisitor.getResult());
    }
}



