#include <string>
#include "nativeLib.h"

int main(int argc, const char * argv[]) {
  if (2 == argc) {
    int input = std::stoi(argv[1]);
    return integerDoubler(input);
  }
  return -1;
}
