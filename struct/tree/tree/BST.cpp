#ifndef fff
#define fff
#include"tree.h"
template<class T>
class bt :public tree<T>
{
	struct node
	{
		T val;
		node*rs, *ls;
		node( const T&value)
		{
			rs = NULL;
			ls = NULL;
			val = value;
		}
	};
	node *root;
	bool search(node *&rt, T w)
	{
		if (rt == NULL)
			return 0;
		else
		{
			if (w > rt->val)
				search(rt->rs, w);
			else if (w < rt->val)
				search(rt->ls, w);
			else
				return 1;
		}
	}
	void insert(node*& rt, T w)
	{
		if (rt == NULL) {
			rt = new node(w);
			return;
		}
		if (w > rt->val)
			insert(rt->rs, w);
		else
			insert(rt->ls, w);
	}
	void erase(node*&rt, T w)
	{
		if (rt == NULL)
			return;
		else if (rt->val > w)
		{
			erase(rt->ls, w);
		}
		else if (rt->val < w)
		{
			erase(rt ->rs, w);
		}
		else
		{
			if (rt->ls == NULL)
				rt = rt->rs;
			else if (rt->rs == NULL)
				rt = rt->ls;
			else
			{
				node *s = rt->rs;
				while (s->ls != NULL) 
					s = s->ls;
				rt->val = s->val;
				erase(rt->rs, s->val);
			}
		}
	}
public:


	bt( const T&val)
	{
		root = new node(val);
	}
	void insert(const T&w)
	{
		insert(root, w);
	}
	void erase(const T&w)
	{
		erase(root, w);
	}
	bool search(const T&w)
	{
		return search(root, w);
	}
};
#endif // !fff