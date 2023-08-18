/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.danius.fireeditor.compression;

import java.util.Arrays;

/**
 * @author Edgar
 */
public class Huffman {
    private static long CMD_CODE = 0x28; // 8-bit Huffman magic number
    private static long HUF_LNODE = 0;
    private static long HUF_RNODE = 1;

    private static long HUF_SHIFT = 1;
    private static long HUF_MASK = 0x80;
    private static long HUF_MASK4 = 0x80000000;

    private static long HUF_LCHAR = 0x80;
    private static long HUF_RCHAR = 0x40;
    private static long HUF_NEXT = 0x3F;

    // const uint RAW_MINIM = 0x0;
    // const uint RAW_MAXIM = 0xFFFFFF;

    // const uint HUF_MINIM = 0x4;
    private int HUF_MAXIM = 0x1400000;

    public static Uint8Array decompressArray(byte[] s) {
    /*var byteArray=getByteArrayFromString(s);
	var fullHex="";
	for(i=0;i<byteArray.length;i++){
		fullHex=fullHex+(binaryToHex(byteArray[i]).result);
	}
	console.log(fullHex);*/
        ///console.log(s.byteLength+" byte length");
        Uint8Array u = new Uint8Array(s);
//System.out.println("Uint8 array created");
        //long i = u.length();
        StringArray a = new StringArray(u.length());

//System.out.println("String array created");
        //DataView dv = new DataView(s, 0);
	
	/*while (i>0){ // map to hex
	i--;
//= (u.get(i) < 16 ? '0' : '') + u.get(i).toString(16);
		
       String hexPart=Integer.toHexString((int)u.get(i));
                    if(hexPart.length()==1){
                        hexPart="0"+hexPart;
                    }
                    a.set(i,hexPart);
	}*/
        //System.out.println("String array filled");
        //u = null; // free memory
        long x = DataView.getUint32(s, 0);
        //System.out.println(x+"x");
///console.log(x);
        long header = x;
        long num_bits = header & 0xF;
//System.out.println("num bits "+num_bits);
///console.log("numbits: "+num_bits);
        long UncompressedLength = header >>> 8;
        //System.out.println(UncompressedLength+" uncompressed length");
        Uint8Array Uncompressed = new Uint8Array(UncompressedLength);
        long pak_pos = 4;
			/*System.out.println("pak_0: "+u.get(0));
			System.out.println("pak_1: "+u.get(1));
                        
			System.out.println("pak_2: "+u.get(2));
			System.out.println("pak_3: "+u.get(3));
                        
			System.out.println("pak_4: "+u.get(4));
                        
			System.out.println("pak_5: "+u.get(5));*/
        ///			console.log("pak_2: "+u[2]);
        ///console.log("pak_3: "+u[3]);s
        //console.log("pak_4: "+u[4]);
        ///console.log("pak_5: "+u[5]);

        ///console.log("pak_alone: "+u[pak_pos]);
        ///console.log("pak_before: "+(u[pak_pos]+1));
        pak_pos = pak_pos + ((u.get(pak_pos) + 1) << 1);

        //System.out.println("pak_position: "+pak_pos);
        ///console.log("pak_position: "+pak_pos);
        long raw_pos = 0;
        long tree_ofs = 4;
        long nbits = 0;
        long pos = u.get(tree_ofs + 1);

        //System.out.println("pos: "+pos);
        long next = 0, mask4 = 0;
        //int counter=0;
        long code = DataView.getUint32(s, (int) pak_pos);
        //System.out.println(code+" code");
        while (raw_pos < Uncompressed.length()) {
            //console.log("msask4 "+mask4);


            mask4 >>>= HUF_SHIFT;

            if ((mask4) == 0) {
                //counter++;
                if ((pak_pos + 3) >= u.length()) {
                    break;
                }
                code = DataView.getUint32(s, (int) pak_pos);
                pak_pos += 4;
                mask4 = Math.abs(HUF_MASK4);

            }
            //System.out.println("mask4 "+mask4);
            //else{
            //console.log("no_mask");
            //}

            next = next + (((pos & HUF_NEXT) + 1) << 1);
            //console.log(next+" nextp");
            long ch;
            if ((code & mask4) == 0) {
                //console.log("Gac");
                ch = pos & HUF_LCHAR;
                pos = u.get(tree_ofs + next);
                //console.log("tree: "+(tree_ofs+next));
            } else {
                ch = pos & HUF_RCHAR;
                pos = u.get(tree_ofs + next + 1);
                //console.log("tree + 1: "+(tree_ofs+next+1));
            }

            if (ch != 0) {
                //console.log(" zzasd, pos "+pos+" nbits "+nbits);
                Uncompressed.set(raw_pos, Uncompressed.get(raw_pos) | (pos << nbits));
                ////  *raw = (*raw << num_bits) | pos;
                nbits = ((nbits + num_bits) & 7);
                //console.log(nbits+" nbits");
                if (nbits == 0) {
                    raw_pos++;
                }

                pos = u.get(tree_ofs + 1);
                next = 0;
            }
            //console.log(raw_pos+" raw");
        }

        //System.out.println("Counter "+counter);
        //return Uncompressed;
        ///console.log(a);
        u = null;
        a = null;


        //var byteArray=getByteArrayFromString(s);

        //console.log(Uncompressed);


//dv.setInt16(1, 42);
//dv.getInt16(1); //42
        return Uncompressed;
        // work with this
    }


    public byte[] compressArray(byte[] n) {
        System.out.println("data length " + n.length);
        byte[] data = new byte[n.length];
        for (int i = 0; i < data.length; i++) {
            if (n[i] < 0) {
                data[i] = (byte) (n[i] & 0xFF);
            } else {
                data[i] = n[i];
            }
        }
        long pk4_pos = 0;
        num_bits = 8;
        long raw_len = (long) data.length;

        byte[] pbuf = new byte[HUF_MAXIM + 1];
        //byte[] aux=Arrays.copyOf(DataView.getBytes((int)((CMD_CODE) | (raw_len << 8))),4);
        System.arraycopy(DataView.getBytes((int) ((CMD_CODE) | (raw_len << 8))),
                0, pbuf, 0, 4);
        //Array.Copy(BitConverter.GetBytes((CMD_CODE) | (raw_len << 8)), pbuf, 4);
        long pak_pos = 4;
        long raw_pos = 0;
        HUF_InitFreqs();

        HUF_CreateFreqs(data, data.length);

        HUF_InitTree();
        HUF_CreateTree();

        HUF_InitCodeTree();
        HUF_CreateCodeTree();
            /*int[] aux=byteToInt(codetree);
            System.out.println("aux-----");
            
            for(int i=0;i<aux.length;i++){
                System.out.println(aux[i]+"");
            }
            System.out.println("-----aux");
            */
        HUF_InitCodeWorks();
        HUF_CreateCodeWorks();

        long cod_pos = 0;
        int clen = codetree[(int) cod_pos];
        if (clen < 0) {
            clen = clen & 0xff;
        }
        long len = (long) ((clen + 1) << 1);

        //System.out.println("LEN "+len);
        while (len-- != 0)
            pbuf[(int) pak_pos++] =
                    codetree[(int) cod_pos++];
        long mask4 = 0;
        while (raw_pos < data.length) {

            long ch = data[(int) raw_pos++];
            if (ch < 0) {
                ch = ch & 0xff;
            }
            //System.out.println(raw_pos+"/"+data.length);

            int nbits;
            for (nbits = 8; nbits != 0; nbits -= num_bits) {
                HuffmanCode code = codes[(int) ch & ((1 << num_bits) - 1)];
                ////  code = codes[ch >> (8 - num_bits)];

                len = code.nbits;
                //System.out.println(len+" lenas 1");

                //if(len<0){
                //    len=len & 0xFF;
                //}
                //System.out.println(len+" lenas 2");

                int cwork = 0;

                //byte mask = (byte)HUF_MASK;
                int mask = Math.abs((int) HUF_MASK);
                while (len-- != 0) {
                    if ((mask4 >>>= Math.abs((int) HUF_SHIFT)) == 0) {
                        mask4 = Math.abs(HUF_MASK4);
                        pk4_pos = pak_pos;
                        System.arraycopy(DataView.getBytes(0), 0, pbuf, (int) pk4_pos, 4);
                        //Array.Copy(BitConverter.GetBytes(0), 0, pbuf, pk4_pos, 4);
                        pak_pos += 4;
                    }
                    int codeworkInt = code.codework[cwork];
                    if (codeworkInt < 0) {
                        codeworkInt = codeworkInt & 0xff;
                    }
                    if ((codeworkInt & mask) > 255) {
                        System.out.println("Greater than -- " + (codeworkInt & mask));
                    }
                    if ((codeworkInt & mask) != 0)
                        System.arraycopy(DataView.getBytes((int) (DataView.getUint32(pbuf, (int) pk4_pos) | (int) mask4)), 0, pbuf, (int) pk4_pos, 4);
                    //Array.Copy(BitConverter.GetBytes(BitConverter.ToUInt32(pbuf, (int)pk4_pos) | mask4), 0, pbuf, pk4_pos, 4);
                    if ((mask >>>= Math.abs((int) HUF_SHIFT)) == 0) {
                        mask = Math.abs((int) HUF_MASK);
                        cwork++;
                    }
                }

                ch >>>= num_bits;
                ////  ch = (ch << num_bits) & 0xFF;
            }
        }
        long pak_len = (long) (pak_pos);
        System.out.println("Done");
        return Arrays.copyOf(pbuf, ((int) pak_len));
    }

    private void HUF_InitFreqs() {


        freqs = new long[(int) max_symbols];

        for (long i = 0; i < max_symbols; i++) freqs[(int) i] = 0;
    }

    private void HUF_CreateFreqs(byte[] raw_buffer, int raw_len) {
        //uint i;

        for (long i = 0; i < raw_len; i++) {
            long ch = raw_buffer[(int) i];
            if (ch < 0) {
                ch = ch & 0xFF;
            }
            // System.out.println(ch+" ch");
            int nbits;
            for (nbits = 8; nbits != 0; nbits -= num_bits) {
                int val = (int) ((long) ch >>> (8 - (long) num_bits));
                //val=Math.abs(val);
                //System.out.println(val);
                freqs[val]++;
                ch = (ch << num_bits) & 0xFF;
                //ch=Math.abs(ch);
            }
        }

        num_leafs = 0;
        for (long i = 0; i < max_symbols; i++) if (freqs[(int) i] != 0) num_leafs++;
        if (num_leafs < 2) {
            if (num_leafs == 1) {
                for (long i = 0; i < max_symbols; i++) {
                    if (freqs[(int) i] != 0) {
                        freqs[(int) i] = 1;
                        break;
                    }
                }
            }
            while (num_leafs++ < 2) {
                for (long i = 0; i < max_symbols; i++) {
                    if (freqs[(int) i] == 0) {
                        freqs[(int) i] = 2;
                        break;
                    }
                }
            }
        }
        num_nodes = (num_leafs << 1) - 1;
    }

    private void HUF_InitTree() {
        tree = new HuffmanNode[(int) num_nodes];
        int i;
        for (i = 0; i < num_nodes; i++) tree[i] = null;
    }

    private void HUF_CreateTree() {


        int num_node = 0;
        for (int i = 0; i < max_symbols; i++) {
            if (freqs[i] != 0) {
                HuffmanNode node = new HuffmanNode();
                tree[num_node++] = node;

                node.symbol = i;
                node.weight = freqs[i];
                node.leafs = 1;
                node.dad = null;
                node.lson = null;
                node.rson = null;
            }
        }


        while (num_node < num_nodes) {
            HuffmanNode rnode;
            HuffmanNode lnode = rnode = null;
            long rweight;
            long lweight = rweight = 0;

            for (int i = 0; i < num_node; i++) {
                if (tree[i].dad == null) {
                    if (lweight == 0 || (tree[i].weight < lweight)) {
                        rweight = lweight;
                        rnode = lnode;
                        lweight = tree[i].weight;
                        lnode = tree[i];
                    } else if (rweight == 0 || (tree[i].weight < rweight)) {
                        rweight = tree[i].weight;
                        rnode = tree[i];
                    }
                }
            }

            HuffmanNode node = new HuffmanNode();
            tree[num_node++] = node;

            node.symbol = num_node - num_leafs + max_symbols;
            node.weight = lnode.weight + rnode.weight;
            node.leafs = lnode.leafs + rnode.leafs;
            node.dad = null;
            node.lson = lnode;
            node.rson = rnode;

            lnode.dad = rnode.dad = node;
        }
    }

    private void HUF_InitCodeTree() {


        long max_nodes = (((num_leafs - 1) | 1) + 1) << 1;

        codetree = new byte[(int) max_nodes];
        codemask = new byte[(int) max_nodes];

        for (int i = 0; i < max_nodes; i++) {
            codetree[i] = 0;
            codemask[i] = 0;
        }
    }

    private void HUF_CreateCodeTree() {
        int i = 0;

        codetree[i] = (byte) ((num_leafs - 1) | 1);

        //System.out.println(codetree[i]+" num leafs");
        codemask[i] = 0;

        HUF_CreateCodeBranch(tree[(int) num_nodes - 1], i + 1, i + 2);
        HUF_UpdateCodeTree();
        int what = codetree[0];
        if (what < 0) {
            what = what & 0xff;
        }

        System.out.println("What " + what);

        int c = (int) ((what + 1) << 1);
        //if(c<0){
        //    c=c & 0xFF;
        //}
        i = c;
        //System.out.println("codetree "+i);
        int[] auxmask = byteToInt(codemask);
        int[] auxtree = byteToInt(codetree);

        while (--i != 0) {
            if (auxmask[i] != 0xFF) {
                auxtree[i] |= auxmask[i];

            }
        }
        codetree = intToByte(auxtree);
    }

    private int HUF_CreateCodeBranch(HuffmanNode root, int p, int q) {
        HuffmanNode[] stack = new HuffmanNode[2 * (int) root.leafs];
        int mask;

        if (root.leafs <= HUF_NEXT + 1) {
            int r;
            int s = r = 0;
            stack[r++] = root;

            while (s < r) {
                HuffmanNode node;
                if ((node = stack[s++]).leafs == 1) {
                    if (s == 1) {
                        codetree[p] = (byte) node.symbol;
                        codemask[p] = (byte) 0xFF;
                    } else {
                        codetree[q] = (byte) node.symbol;
                        codemask[q++] = (byte) 0xFF;
                    }
                } else {
                    mask = 0;
                    if (node.lson.leafs == 1) mask |= HUF_LCHAR;
                    if (node.rson.leafs == 1) mask |= HUF_RCHAR;

                    if (s == 1) {
                        codetree[p] = (byte) ((r - s) >>> 1);
                        codemask[p] = (byte) mask;
                    } else {
                        codetree[q] = (byte) ((r - s) >>> 1);
                        codemask[q++] = (byte) mask;
                    }

                    stack[r++] = node.lson;
                    stack[r++] = node.rson;
                }
            }
        } else {
            mask = 0;
            if (root.lson.leafs == 1) mask |= HUF_LCHAR;
            if (root.rson.leafs == 1) mask |= HUF_RCHAR;

            codetree[p] = 0;
            codemask[p] = (byte) mask;

            if (root.lson.leafs <= root.rson.leafs) {
                int l_leafs = HUF_CreateCodeBranch(root.lson, q, q + 2);
                int r_leafs = HUF_CreateCodeBranch(root.rson, q + 1, q + (l_leafs << 1));
                codetree[q + 1] = (byte) (l_leafs - 1);
            } else {
                int r_leafs = HUF_CreateCodeBranch(root.rson, q + 1, q + 2);
                int l_leafs = HUF_CreateCodeBranch(root.lson, q, q + (r_leafs << 1));
                codetree[q] = (byte) (r_leafs - 1);
            }
        }

        return ((int) root.leafs);
    }

    public static byte[] intToByte(int[] var) {
        byte[] arreglo = new byte[var.length];
        for (int i = 0; i < arreglo.length; i++) {

            arreglo[i] = (byte) var[i];
        }
        return arreglo;
    }

    public static int[] byteToInt(byte[] var) {
        int[] arreglo = new int[var.length];
        for (int i = 0; i < arreglo.length; i++) {
            int c = var[i];
            if (c < 0) {
                c = c & 0xff;
            }
            arreglo[i] = c;
        }
        return arreglo;
    }

    private void HUF_UpdateCodeTree() {
        int i;

        //int what=codetree[0];
        //if(what<0){
        //    what=what &0xff;
        //}


        int auxtree[] = byteToInt(codetree);
        int auxmask[] = byteToInt(codemask);
        int max = (int) ((auxtree[0] + 1) << 1);

        for (i = 1; i < max; i++) {

            if ((auxmask[i] != 0xFF) && (auxtree[i] > HUF_NEXT)) {
                long inc;
                if ((i & 1) != 0 && (auxtree[i - 1] == HUF_NEXT)) {
                    i--;
                    inc = 1;
                } else if ((i & 1) == 0 && (auxtree[i + 1] == HUF_NEXT)) {
                    i++;
                    inc = 1;
                } else {
                    inc = auxtree[i] - Math.abs((int) HUF_NEXT);
                }
                //if(codetree[i]<0){
                //    System.out.println("negative codetree "+codetree[i]);
                //    codetree[i]=(byte)(codetree[i] & 0xFF);
                //}
                long n1 = (i >>> 1) + 1 + auxtree[i];
                long n0 = n1 - inc;

                long l1 = n1 << 1;
                long l0 = n0 << 1;
                codetree = intToByte(auxtree);
                codemask = intToByte(auxmask);

                long tmp0 = DataView.getUint16(codetree, (int) l1);
                long tmp1 = DataView.getUint16(codemask, (int) l1);
                long j;
                for (j = l1; j > l0; j -= 2) {
                    System.arraycopy(DataView.getBytes((int) (DataView.getUint16(codetree, (int) j - 2))), 0, codetree, (int) j, 2);
                    System.arraycopy(DataView.getBytes((int) (DataView.getUint16(codemask, (int) j - 2))), 0, codemask, (int) j, 2);

                    //Array.Copy(BitConverter.GetBytes(BitConverter.ToUInt16(codetree, (int)j - 2)), 0, codetree, (int)j, 2);
                    //Array.Copy(BitConverter.GetBytes(BitConverter.ToUInt16(codemask, (int)j - 2)), 0, codemask, (int)j, 2);
                }
                System.arraycopy(DataView.getBytes((int) tmp0), 0, codetree, (int) l0, 2);
                System.arraycopy(DataView.getBytes((int) tmp1), 0, codemask, (int) l0, 2);

                //Array.Copy(BitConverter.GetBytes(tmp0), 0, codetree, l0, 2);
                //Array.Copy(BitConverter.GetBytes(tmp1), 0, codemask, l0, 2);
                auxtree = byteToInt(codetree);
                auxmask = byteToInt(codemask);

                auxtree[i] -= inc;

                long k;
                for (j = i + 1; j < l0; j++) {
                    if (auxmask[(int) j] != 0xFF) {
                        k = (j >>> 1) + 1 + auxtree[(int) j];
                        if ((k >= n0) && (k < n1)) auxtree[(int) j]++;
                    }
                }

                if ((auxmask[(int) l0 + 0]) != 0xFF)
                    auxtree[(int) l0 + 0] += inc;
                if (auxmask[(int) l0 + 1] != 0xFF)
                    auxtree[(int) l0 + 1] += (byte) inc;

                for (j = l0 + 2; j < l1 + 2; j++) {
                    if (auxmask[(int) j] != 0xFF) {
                        k = (j >>> 1) + 1 + auxtree[(int) j];
                        if (k > n1) auxtree[(int) j]--;
                    }
                }
                codetree = intToByte(auxtree);
                codemask = intToByte(auxmask);

                i = (i | 1) - 2;
            }
        }
        codetree = intToByte(auxtree);
        codemask = intToByte(auxmask);

    }

    private void HUF_InitCodeWorks() {
        int i;
        codes = new HuffmanCode[(int) max_symbols];
        for (i = 0; i < max_symbols; i++) codes[i] = null;
    }

    private void HUF_CreateCodeWorks() {
        byte[] scode = new byte[100];
        int i;

        for (i = 0; i < num_leafs; i++) {
            HuffmanNode node = tree[i];
            long symbol = node.symbol;

            long nbits = 0;
            while (node.dad != null) {
                scode[(int) nbits++] = node.dad.lson == node ? (byte) HUF_LNODE : (byte) HUF_RNODE;
                node = node.dad;
            }
            long maxbytes = (nbits + 7) >>> 3;

            HuffmanCode code = new HuffmanCode();

            codes[(int) symbol] = code;
            code.nbits = nbits;
            code.codework = new byte[(int) maxbytes];

            int j;
            for (j = 0; j < maxbytes; j++) code.codework[j] = 0;
            int mask = Math.abs((int) HUF_MASK);
            //byte mask = (byte)HUF_MASK;
            j = 0;
            long nbit;
            for (nbit = nbits; nbit != 0; nbit--) {
                if (scode[(int) nbit - 1] != 0) code.codework[j] |= mask;
                if ((mask >>>= HUF_SHIFT) == 0) {
                    mask = Math.abs((int) HUF_MASK);
                    j++;
                }
            }
        }
    }


    private class HuffmanNode {
        public long symbol;
        public long weight;
        public long leafs;

        public HuffmanNode dad;
        public HuffmanNode lson;
        public HuffmanNode rson;
    }

    private class HuffmanCode {
        public long nbits;
        public byte[] codework;
    }

    HuffmanNode[] tree;
    byte[] codetree, codemask;
    HuffmanCode[] codes;
    final long max_symbols = 0x100;
    long num_leafs, num_nodes;
    long[] freqs;
    int num_bits;
}
