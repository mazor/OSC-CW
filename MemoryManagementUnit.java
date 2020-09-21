import java.util.LinkedList;

public class MemoryManagementUnit {
    /** The page table */
    private PageTableEntry[] pageTable;
    
    /** How many bits of logical memory addresses are used for the offset */
    private int bitsForOffset;
    
    /** A bitmask, where the bits for the offset are set to 1; and everything else is 0 */
    private int offsetMask;
    
    /** A bitmask, where the bits for the page number are set to 1; and everything else is 0 */
    private int pageNumberMask;
    
    /** The free frames list */
    private LinkedList<Integer> freeFrames;
    private static int i=0;
    /** Create an MMU
     *
     * @param pageTableSize  The number of entries in the page table
     * @param bitsForOffset  How many bits of logical memory addresses are used for the offset.  The rest are assumed to be the page number.
     */
    public MemoryManagementUnit(int pageTableSize, int bitsForOffset) {
        this.bitsForOffset = bitsForOffset;
        
        offsetMask = 0;
        for (int o = 0; o < bitsForOffset; ++o) {
            offsetMask = offsetMask | (1 << o);
        }
        
        pageNumberMask = ~offsetMask;
        
        
        pageTable = new PageTableEntry[pageTableSize];
        
        // Initialise all the page table entries to invalid, by making default page table entries
        for (int i = 0; i < pageTableSize; ++i) {
            pageTable[i] = new PageTableEntry();
        }
        
        freeFrames = new LinkedList<>();
        
        // assume there are half as many frames, as there are pages        
        for (int i = 0; i < (pageTableSize / 2); ++i) {
            freeFrames.addLast((2 << bitsForOffset) * i);
        }
        
    }
    
    /** Access a memory address; returning true if there was a page fault */
    public boolean accessMemoryAddress(int address) {
        int offset = address & offsetMask;
        int pageNumber = (address & pageNumberMask) >> bitsForOffset;
        
      //  System.out.println("Accessing memory with page " + pageNumber + ", offset " + offset);
        
        if (pageTable[pageNumber].getValidBit()) {
            // no page fault, woot
            // set the reference bit            
            pageTable[pageNumber].setReferenceBit(true);            
            return false; // no page fault - return false
        } else {
            pageFaultHandler(pageNumber);
            return true; // page fault - return true
        }
    }
    
    /** An implementation of the second-chance page-fault handler algorithm
     *
     * Your task is to implement this
     *
     * @param pageNumber
     */
    private void pageFaultHandler(int pageNumber) {
		
		// TODO: your code goes here
        // Implement the second-chance page-fault handler algorithm
        // (Don't forget to check if there is a free frame)
		
		if (pageNumber>=0 && pageNumber<pageTable.length){
			
			boolean found=false; //initially assumed to not have been found in the pagetable
			
			if((pageTable[pageNumber].getValidBit())){ //if it was found then
				
				pageTable[pageNumber].setValidBit(true); //valid is set to be true
				pageTable[pageNumber].setReferenceBit(true); //reference set to be true
			
				found=true; //found is assigned true
				
			}
			
			//if it wasn't found
			if (!found){
			
				if (!(freeFrames.isEmpty())){  //if there is at least one free frame left
					
					int fff = freeFrames.getFirst(); //get the first free frame
					
					PageTableEntry pte = new PageTableEntry(fff); //create a new PageTableEntry with the address fff
					
					pte.setValidBit(false); //valid bit set to false
						
					freeFrames.remove(0); //remove the first frame from freeFrame
					
					pageTable[pageNumber] = pte; //assign pte to the pageTable slot with index pageNumber
					
					//reference and valid bits set to be true
					pageTable[pageNumber].setValidBit(true); 
					pageTable[pageNumber].setReferenceBit(true);
					pte.setReferenceBit(true);
					pte.setValidBit(true);
			
		
				} else { //need to choose a victim
							
					PageTableEntry page = pageTable[i];
				
					while ((page.getReferenceBit()) || (!page.getValidBit())){
					
						pageTable[i].setReferenceBit(false);
						++i;
		
						if (i>=pageTable.length){
							i=0;
						}
						
						page = pageTable[i];
					}

					
					pageTable[i].setValidBit(false);
					pageTable[i].setReferenceBit(false);
					PageTableEntry vic = page;

					vic.setValidBit(false);
			
					PageTableEntry anotherPage =  new PageTableEntry(vic.getAddress());
					pageTable[i]=new PageTableEntry();
					
					anotherPage.setValidBit(true);
					pageTable[pageNumber]=anotherPage;
					pageTable[pageNumber].setValidBit(true);
					pageTable[pageNumber].setReferenceBit(true);
					i++;
					
					if (i>=pageTable.length){
							i=0;
					}
			}
			
			}
		
		}
    }
}
