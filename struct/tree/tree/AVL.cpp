#pragma once
#include"tree.h"
template<class T>
class AVL:public tree<T> {
	struct node
	{
		T val;
		node*rs, *ls;
		int height;
		node(const T&value)
		{
			rs = NULL;
			ls = NULL;
			val = value;
			height = 1;
		}
	};
	void rotate(node *&rt,int t)
	{

		node *tp = rt;
		if (t == 1)
		{
			rt = rt->ls;
			rt->height++;
			if(rt->ls!=NULL)
			rt->ls->height++;
			tp->ls = rt->rs;
			tp->height--;
			rt->rs = tp;
		}
		else
		{
			rt = rt->rs;
			rt->height++;
			if(rt->rs!=NULL)
			rt->rs->height++;
			tp->rs = rt->ls;
			tp->height--;
			rt->ls = tp;
		}
	}
	inline int getrs(node *rt) { return rt->rs != NULL?rt->rs->height:0; }
	inline int getls(node *rt) { return rt->ls != NULL?rt->ls->height:0; }
	inline int geth(node *rt) { int n = getls(rt) - getrs(rt); return n; }
	inline int getmax(node* rt) {
		return max(getrs(rt), getls(rt))+1;
	}
	void weihu(node *&nd)
	{
		nd->height = getmax(nd);
		int ph = geth(nd);
		if (ph == 2)
		{
			if (geth(nd->ls) < 0) rotate(nd->ls, 0);
			rotate(nd, 1);
		}
		else if(ph==-2)
		{
			if (geth(nd -> rs) > 0) rotate(nd->rs, 1);
			rotate(nd, 0);
		}
	}
	void insert(node *&rt,const T&w) 
	{
		if (rt == NULL)
			rt = new node(w);
		else if (rt->val > w)
			insert(rt->ls, w);
		else
			insert(rt->rs, w);
		weihu(rt);
	}
	void erase(node *&rt,const T&w)
	{
		if (rt->val > w)
		{
			erase(rt->ls,w);
		}
		else if (rt->val < w)
		{
			erase(rt->rs, w);
		}
		else
		{
			if (rt->ls == NULL)
			{
				rt = rt->rs;
			}
			else if(rt->rs==NULL)
			{
				rt = rt->ls;
			}
			else {
				node* p = rt;
				p = p->rs;
				while (p->ls != NULL)
					p = p->ls;
				rt->val = p->val;
				if (rt->rs->ls == NULL)
					rt->rs=NULL;
				else
				{
					rt = rt->rs;
					while (rt->ls->ls != NULL)
					{
						rt = rt->ls;
					}
					rt->ls = NULL;
				}
			}
		}
		weihu(rt);
	}
public:
	node *root;
	AVL(const T&w)
	{
		root = new node(w);
	}
	~AVL()
	{
		delete root;
	}
		void insert(const T&w)
	{
		insert(root, w);
	}
	void erase(const T&w)
	{
		erase(root, w);
	}
};