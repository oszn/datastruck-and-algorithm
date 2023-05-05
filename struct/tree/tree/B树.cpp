#pragma once
#include"tree.h"
#include<vector>
#define pb push_back
template<class T,int N>
class BTree:public tree<T> {
	struct node
	{
		vector<T> keys;//关键字
		vector<node*> sons;//儿子们
		bool leaf;
		node(const T&w)
		{
			keys.push_back(w);
			leaf = true;
		}
		node()
		{
			leaf = true;
		}
		
	};
	void vec_ins(vector<T>&p,const T&w)
	{
		int i = 0;
		for (; i < p.size(); i++)
		{
			if (w == p[i])
				return;
			if (w < p[i]) {
				p.insert(p.begin() + i, w);
				break;
			}
		}
		if (i == p.size())
			p.pb(w);
	}
	void merge(vector<T> r1, vector<T>&r2)
	{
		for (int i = 0; i < r1.size(); i++)
		{
			vec_ins(r2, r1[i]);
		}
	}
	void ep(node*&rt, node*father,int px)
	{
		if (rt->leaf == true)
		{
			if (rt->keys.size() <=N / 2 - 1)
			{
				//vector<T> temp = rt->keys();
				node*tx;
				if (px != father->sons.size()-1)
				{
					if (father->sons[px + 1]->keys.size()
						+ 
						rt->keys.size() <=N - 1)
					{
						vec_ins(father->sons[px + 1]->keys, father->keys[px]);
						father->keys.erase(father->keys.begin());
						merge(rt->keys, father->sons[px + 1]->keys);
						father->sons.erase(father->sons.begin()+px);
					}
					else
					{
						vec_ins(rt->keys, father->keys[0]);
						int n = father->sons[px + 1]->keys[0];
						father->sons[px + 1]->keys.erase(father->sons[px + 1]->keys.begin());
						father->keys[0] = n;
					}
					//vec_ins(father->sons[px + 1], father->keys[px]);
					//father->keys.erase(father->keys.begin());
					//merge(rt->keys, father[px + 1]->keys);
					//father->sons.erase(sons.begin());
				}
				else
				{
					if (father->sons[px -1]->keys.size() + rt->keys.size() <= N - 1)
					{
						vec_ins(father->sons[px -1]->keys, father->keys[px-1]);
						father->keys.erase(father->keys.end()-1);
						merge(rt->keys, father->sons[px-1]->keys);
						father->sons.erase(father->sons.end()-1);
					}
					else
					{
						vec_ins(rt->keys, father->keys[father->keys.size()]);
						int n = father->sons[px + 1]->keys[father->keys.size()];
						father->sons[px -1]->keys.erase(father->sons[px -1]->keys.end()-1);
						father->keys.pb(n);
					}
				}
			}
		}
	}
	void erase(node*&rt,const T&w, node*father,int ps)
	{
		for (int i = 0; i < rt->keys.size(); i++)
		{
			if (rt->keys[i] == w)
			{
				if (rt->leaf != true) {
					node* tp = rt->sons[i];
					node* ftp = rt->sons[i];
					int fa = 0;
					while (tp->sons.size() != 0)
					{
						if (fa != 0)
							ftp = ftp->sons[ftp->sons.size() - 1];
						tp = tp->sons[tp->sons.size()-1];
						fa++;
					}
					if (tp == ftp)
						ftp = rt;
					int n = tp->keys[tp->keys.size() - 1];
					rt->keys[i] = n;
					ep(tp, ftp, ftp->keys.size()-1);

				}
				else
				{
					ep(rt, father, i);
					return;
				}
			}
			else if (rt->keys[i] > w)
			{
				if (rt->leaf == true)
					return;
				erase(rt->sons[i],w,rt,i);
			}
			else if (i == rt->keys.size() - 1)
			{
				if (rt->leaf == true)
					return;
				erase(rt->sons[i+1],w,rt,i+1);
			}
		}
	}
	void weihu(node*&rt,node*father,int k=0)
	{
		if (rt->keys.size() > N - 1)
		{
			if (root == rt)
			{
				father = new node();
				father->sons.pb(rt);
			}
			node *ne = new node();
			for (int i = N / 2 + 1; i < rt->keys.size();)
			{
				ne->keys.pb(rt->keys[i]);
				rt->keys.erase(rt->keys.begin() + i);
			}
			int key = rt->keys[N / 2];
			rt->keys.erase(rt->keys.begin() + N / 2);
			for (int i = N / 2 + 1; i < rt->sons.size();)
			{
				ne->sons.pb(rt->sons[i]);
				rt->sons.erase(rt->sons.begin() + i);
			}
			ne->leaf = false;
			rt->leaf = false;
			if (k == 1) {
				ne->leaf = true;
				rt->leaf = true;
			}
			father->leaf = false;
			father->sons.pb(ne);
			vec_ins(father->keys, key);
			if (rt == root)
			{
				rt = father;
			}
		}
	}
	void insert(node *&rt,const T&w,node* father=nullptr,int fap=0)
	{
		if (rt->leaf)
		{
			vec_ins(rt->keys, w);
			weihu(rt, father,1);
		}
		else
		{
			for (int i = 0; i < rt->keys.size(); i++)
			{
				if (rt->keys[i] == w)
					return;
				if (rt->keys[i] > w)
				{
					insert(rt->sons[i], w, rt, i);
					weihu(rt, father);
					break;
				}
				if (i == rt->keys.size() - 1) {
					insert(rt->sons[i + 1], w, rt, i + 1);
					weihu(rt, father);
					break;
				}
			}
		}
	}
	node *root;
	void pre(node*rt, int k)
	{
		if (rt->leaf == true)
			cout << k;
		else
		for (int i = 0; i < rt->sons.size(); i++)
		{
				pre(rt->sons[i],k + 1);
		}
	}
public:
	BTree(const T&w)
	{
		root = new node(w);
	}
	void insert(const T&w)
	{
		insert(root, w, nullptr, 0);
	}
	void erase(const T&w)
	{
		erase(root, w,nullptr,0);
	}
	void pre()
	{
		pre(root, 0);
	}
};